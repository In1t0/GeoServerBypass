package me.gv7.woodpecker.plugin;

//import me.gv7.woodpecker.plugin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class XMLPayloadBypass implements IHelper {

    @Override
    public String getHelperTabCaption() {
        return "XML Payload Bypass";
    }

    @Override
    public IArgsUsageBinder getHelperCutomArgs() {
        IArgsUsageBinder argsUsageBinder = VulPluginInfo.pluginHelper.createArgsUsageBinder();
        List<IArg> args = new ArrayList<IArg>();
        IArg args1 = VulPluginInfo.pluginHelper.createArg();
        args1.setName("all");
        args1.setDefaultValue("");
        args1.setDescription("write text");
        args1.setRequired(true);
        args.add(args1);
        argsUsageBinder.setArgsList(args);
        return argsUsageBinder;
    }

    private static final List<String> blacklist = Arrays.asList(
            "eval", "getEngineByName", "javax.script", "ScriptEngineManager", "java.lang.Class",
            "sun.misc", "BASE64Decoder", "newInstance", "java.util.Base64", "sun.misc.Unsafe",
            "getDeclaredField", "setAccessible", "unsafe", "defineAnonymousClass", "SpelExpressionParser",
            "cglib.core", "ReflectUtils", "java.lang.Thread", "getContextClassLoader", "forName", "ExpressionParser",
            "toString", "getValue", "Base64Utils", "defineClass", "currentThread", "ClassLoader", "Runtime",
            "classBytes", "exec", "springframework", "java.net.InetAddress", "bash", "theUnsafe", "getDecoder", "decodeBuffer",
            "decode", "util", "IOUtils", "toByteArray", "GZIPInputStream", "ByteArrayInputStream", "decodeFromString",
            "apache"
    );

    private static String generateEntityName() {
        return "e_" + UUID.randomUUID().toString().substring(0, 8);
    }

    // 存储实体定义
    private static List<String> entityDefinitions = new ArrayList<>();
    // 替换词典
    private static Map<String, String> replacementMap = new HashMap<>();

    private static List<String> splitRandomly(String word) {
        int splitCount = new Random().nextInt(3) + 2; // 随机选择1-3段
        if (word.length() <= splitCount) {
            return Collections.singletonList(word);  // 如果长度小于分段数，则不分割
        }

        // 生成随机的切割位置
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < splitCount - 1) {
            indices.add(new Random().nextInt(word.length() - 1) + 1);
        }

        List<Integer> sortedIndices = new ArrayList<>(indices);
        Collections.sort(sortedIndices);

        List<String> parts = new ArrayList<>();
        int prev = 0;
        for (int idx : sortedIndices) {
            parts.add(word.substring(prev, idx));
            prev = idx;
        }
        parts.add(word.substring(prev));

        return parts;
    }

    public static String replaceBlacklist(String xml) {

        for (String keyword : blacklist) {
            // 遍历黑名单关键词，找到每个出现的位置
            Pattern pattern = Pattern.compile(Pattern.quote(keyword));
            Matcher matcher = pattern.matcher(xml);

            while (matcher.find()) {
                String fullWord = matcher.group(0);
                if (!replacementMap.containsKey(fullWord)) {
                    List<String> parts = splitRandomly(fullWord);
                    List<String> entityNames = new ArrayList<>();

                    for (String part : parts) {
                        String entityName = generateEntityName();
                        entityDefinitions.add("<!ENTITY " + entityName + " \"" + part + "\">");
                        entityNames.add("&" + entityName + ";");
                    }
                    replacementMap.put(fullWord, String.join("", entityNames));
                }
            }
        }

        // 替换为实体
        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            xml = xml.replace(entry.getKey(), entry.getValue());
        }

        return xml;
    }

    @Override
    public void doHelp(Map<String, Object> customArgs, IResultOutput iResultOutput) {
        String payloadPre = (String)customArgs.get("all");
        String payload = payloadPre.replaceFirst("<\\?xml.*\\?>", "");

        try {

            iResultOutput.rawPrintln("\n");
            String modifiedXml = replaceBlacklist(payload);
            iResultOutput.rawPrintln("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            iResultOutput.rawPrintln("<!DOCTYPE root [");
            for (String entity : entityDefinitions) {
                iResultOutput.rawPrintln("    " + entity);
            }
            iResultOutput.rawPrintln("]>");
            modifiedXml = modifiedXml.replaceFirst("^\\s*", "");
            iResultOutput.rawPrintln(modifiedXml);
            iResultOutput.rawPrintln("\n");

            entityDefinitions.clear();
            replacementMap.clear();

        }catch (Exception e){}
    }

}
