package apinexo.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import apinexo.common.dtos.MetaDto;
import apinexo.common.dtos.Response;

@Component
@Primary
public class ApinexoUtils {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    protected Random random;

    private static final String HEX_CHARACTERS = "0123456789abcdef";

    private final String OS = System.getProperty("os.name");

    public <T> ResponseEntity<T> callApi(String endpoint, HttpMethod method, Map<String, Object> requestBody,
            Class<T> responseType, HttpHeaders headers) {
        if (Objects.isNull(headers))
            headers = buildHeader();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(endpoint, method, requestEntity, responseType);
    }

    public HttpHeaders buildHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public HttpHeaders buildHeader(String operationName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    };

    public HttpHeaders buildHeaderRandom(HttpHeaders originalHeaders) {
        HttpHeaders newHeaders = new HttpHeaders();
        List<String> keys = new ArrayList<>(originalHeaders.keySet());
        Collections.shuffle(keys);
        for (String key : keys) {
            newHeaders.set(key, originalHeaders.getFirst(key));
        }
        return newHeaders;
    }

    public HttpHeaders buildHeaderForm() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    public Response ok(Object data) {
        Response response = new Response();
        response.setData(data);
        return response;
    }

    public Response ok(Object data, String message) {
        Response response = new Response();
        response.setData(data);
        return response;
    }

    public Response ok(Object data, MetaDto meta, String message) {
        Response response = new Response();
        response.setData(data);
        response.setMeta(meta);
        return response;
    }

    public Response ok(Object data, MetaDto meta) {
        Response response = new Response();
        response.setData(data);
        response.setMeta(meta);
        return response;
    }

    public JsonNode ok(Object data, JsonNode meta) {
        Response response = new Response();
        response.setData(data);
        JsonNode responseNode = convertDtoToJson(response);
        ((ObjectNode) responseNode).set("meta", meta);
        return responseNode;
    }

    public JsonNode ok(Object data, ObjectNode meta) {
        Response response = new Response();
        response.setData(data);
        JsonNode responseNode = convertDtoToJson(response);
        ((ObjectNode) responseNode).set("meta", meta);
        return responseNode;
    }

    public MetaDto getMetaDto(Integer page, Integer resultsPerPage, Integer totalRecords) {
        MetaDto metaDto = new MetaDto();
        metaDto.setPage(page);
        metaDto.setLimit(resultsPerPage);
        metaDto.setTotalRecords(totalRecords);
        if (Objects.nonNull(resultsPerPage) && resultsPerPage > 1 && Objects.nonNull(totalRecords))
            metaDto.setTotalPage((totalRecords + resultsPerPage - 1) / resultsPerPage);
        else
            metaDto.setTotalPage(totalRecords);
        return metaDto;
    }

    public JsonNode getMetaDto() {
        MetaDto metaDto = this.getMetaDto(null, null, 0);
        metaDto.setTotalPage(null);
        metaDto.setTotalRecords(null);
        return convertDtoToJson(metaDto);
    }

    public Response err(Object errors, String... message) {
        Response response = new Response();
        Object errorsJson = convertStrToJson(Objects.nonNull(errors) ? errors.toString() : null);
        if (Objects.nonNull(errorsJson)) {
            errors = errorsJson;
        }
        response.setMessage(errors);
        return response;
    }

    public Object err2(Object errors, String... message) {
        Object errorsJson = convertStrToJson(Objects.nonNull(errors) ? errors.toString() : null);
        if (Objects.nonNull(errorsJson)) {
            errors = errorsJson;
        }
        return errors;
    }

    public Response errPython(Object errors, String... message) {
        Object errorsJson = convertStrToJson(Objects.nonNull(errors) ? errors.toString() : null);
        if (Objects.nonNull(errorsJson)) {
            errors = errorsJson;
            if (errorsJson instanceof JsonNode) {
                JsonNode errorJson = (JsonNode) errors;
                JsonNode errorsText = dataInJson(convertStrToJson(errorJson.toString()), "content");
                JsonNode jsonNode = convertStrToJson(errorsText.asText());
                errors = jsonNode;
            }
        }
        return err(errors, message);
    }

    public Response errStatusTrue(Object errors, String... message) {
        Response response = new Response();
        Object errorsJson = convertStrToJson(Objects.nonNull(errors) ? errors.toString() : null);
        if (Objects.nonNull(errorsJson)) {
            errors = errorsJson;
        }
        response.setMessage(errors);
        return response;
    }

    public String encodeToBase64(String originalString) {
        try {
            return new String(Base64.encodeBase64(originalString.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    public String decodeFromBase64(String encodedString) {
        try {
            if (!isBase64(encodedString)) {
                return null;
            }
            return new String(Base64.decodeBase64(encodedString.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    public String encodeToBase64WithCompress(String originalString) {
        try {
            byte[] compressedData = compress(originalString);
            return java.util.Base64.getEncoder().encodeToString(compressedData);
        } catch (Exception e) {
            return null;
        }
    }

    public String decodeFromBase64WithCompress(String encodedString) {
        try {
            byte[] decodedData = java.util.Base64.getDecoder().decode(encodedString);
            return decompress(decodedData);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] compress(String data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data.getBytes());
        deflater.finish();

        byte[] buffer = new byte[1024];
        int length = deflater.deflate(buffer);
        deflater.end();

        byte[] output = new byte[length];
        System.arraycopy(buffer, 0, output, 0, length);
        return output;
    }

    public String decompress(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            length = inflater.inflate(buffer);
        } catch (DataFormatException e) {
        }
        inflater.end();

        return new String(buffer, 0, length);
    }

    public String decodeUrl(String url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, "UTF-8");
    }

    public JsonNode convertStrToJson(String jsonString) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        try {
            return mapper.readTree(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    public JsonNode convertTextNodeToJson(JsonNode jsonNode) {
        if (Objects.isNull(jsonNode)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (jsonNode instanceof TextNode) {
                TextNode textNode = (TextNode) jsonNode;
                String jsonString = textNode.asText();
                return objectMapper.readTree(jsonString);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public <T> T convertStrToObj(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> List<T> convertStrToObjLst(String jsonString, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            return null;
        }
    }

    public <T> JsonNode convertLstToJson(List<T> list) {
        try {
            return new ObjectMapper().valueToTree(list);
        } catch (Exception e) {
            return null;
        }
    }

    public ObjectNode convertStrToObjNode(String jsonString) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            if (jsonNode instanceof ObjectNode) {
                return (ObjectNode) jsonNode;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDate getDateYYYYMMDD(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDateTime getDateYYYYMMDDHHMM(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            return LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalDateTime getDateYYYYMMDDHHMMSS(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            return LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public TemporalAccessor getDateYYYYMM(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            return formatter.parse(date);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public TemporalAccessor getDateMMDD(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            return formatter.parse(date);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public LocalTime getTimeHHMM(String timeStr) {
        try {
            if (StringUtils.isEmpty(timeStr)) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse(timeStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isValidTimeFormat(String time, String character) {
        try {
            if (StringUtils.isEmpty(time)) {
                return false;
            }
            return time.matches(String.format("\\d{2}%s\\d{2}", character));
        } catch (Exception e) {
            return false;
        }
    }

    public int convert2Minutes(LocalTime localTime) {
        try {
            if (Objects.isNull(localTime)) {
                return 0;
            }
            return localTime.getHour() * 60 + localTime.getMinute();
        } catch (Exception e) {
            return 0;
        }
    }

    public int convert2Minutes(String time, String character) {
        try {
            if (Objects.isNull(time)) {
                return 0;
            }
            String[] parts = time.split(character);
            if (parts.length < 2) {
                return 0;
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (Exception e) {
            return 0;
        }
    }

    public long getMillisecond(LocalTime time) {
        if (Objects.isNull(time))
            return 0;
        return time.toNanoOfDay() / 1_000_000;
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now(ZoneId.of(ConstantUtils.TIME_ZONE_UCT));
    }

    public LocalDate getCurrentDate(String timeZone) {
        return LocalDate.now(ZoneId.of(timeZone));
    }

    public LocalDateTime getCurrentDateTime(LocalDate localDate, String timeZone) {
        return LocalDateTime.of(localDate, LocalTime.now(ZoneId.of(timeZone)));
    }

    public LocalDateTime getCurrentDateTime(String timeZone) {
        return LocalDateTime.now(ZoneId.of(timeZone));
    }

    public String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (Objects.isNull(dateTime) || StringUtils.isBlank(pattern))
            return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public List<Integer> convertStrToIntLst(String stringInput) {
        try {
            if (StringUtils.isBlank(stringInput)) {
                return Collections.emptyList();
            }
            String[] stringArray = stringInput.split(",");
            return Arrays.stream(stringArray).map(Integer::parseInt).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<String> convertStrToStrLst(String stringInput) {
        try {
            if (StringUtils.isBlank(stringInput)) {
                return Collections.emptyList();
            }
            String[] stringArray = stringInput.split(",");
            return Arrays.stream(stringArray).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public <T> List<T> convertJsonNodeToLst(JsonNode jsonNode, Class<T> clazz) {
        try {
            List<T> list = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode element : jsonNode) {
                    T obj = objectMapper.treeToValue(element, clazz);
                    list.add(obj);
                }
            }
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public JsonNode dataInJson(JsonNode json, String... keys) {
        if (Objects.isNull(json))
            return null;

        for (String key : keys) {
            if (json.isObject() && json.has(key)) {
                json = json.get(key);
            } else {
                return null;
            }
        }
        return json;
    }

    public String addOrUpdateParameter(String url, String paramName, String paramValue) {
        try {
            URIBuilder builder = new URIBuilder(url);
            List<NameValuePair> queryParams = builder.getQueryParams();
            boolean paramExists = false;
            for (int i = 0; i < queryParams.size(); i++) {
                NameValuePair param = queryParams.get(i);
                if (param.getName().equals(paramName)) {
                    paramExists = true;
                    queryParams.set(i, new org.apache.http.message.BasicNameValuePair(paramName, paramValue));
                    break;
                }
            }
            if (!paramExists) {
                queryParams.set(queryParams.size() - 1,
                        new org.apache.http.message.BasicNameValuePair(paramName, paramValue));
            }
            builder.setParameters(queryParams);
            URI modifiedUri = builder.build();
            return modifiedUri.toString();
        } catch (Exception e) {
            return url;
        }
    }

    public <T> T readJsonFile(String filePath, Class<T> clazz) throws IOException {
        if (!isLinux()) {
            File currentDirectory = ResourceUtils.getFile("src/main/resources");
            filePath = currentDirectory.getAbsolutePath() + File.separator + filePath;
            return objectMapper.readValue(new File(filePath), clazz);
        } else {
            File currentDirectory = ResourceUtils.getFile("classpath:");
            filePath = currentDirectory.getAbsolutePath() + File.separator + filePath;
            return objectMapper.readValue(new File(filePath), clazz);
        }
    }

    public String readTextFile(String filePath) throws IOException {
        if (!isLinux()) {
            Path pathToFile = Paths.get("src/main/resources", filePath);
            byte[] bytes = Files.readAllBytes(pathToFile);
            String rawContent = new String(bytes, StandardCharsets.UTF_8);
            return StringEscapeUtils.unescapeJava(rawContent);
        } else {
            Resource resource = resourceLoader.getResource("classpath:" + File.separator + filePath);
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String rawContent = new String(bytes, StandardCharsets.UTF_8);
            return StringEscapeUtils.unescapeJava(rawContent);
        }
    }

    public void saveToFile(String data, String pathFile) throws IOException {
        Path filePath;
        if (!isLinux()) {
            filePath = Paths.get("src/main/resources", pathFile);
        } else {
            filePath = Paths.get(resourceLoader.getResource("classpath:" + pathFile).getURI());
        }
        try (OutputStream os = new FileOutputStream(filePath.toFile())) {
            os.write(data.getBytes());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public String uuidRandom() {
        return UUID.randomUUID().toString();
    }

    public String convertObjNodeToParams(JsonNode jsonNode) {
        try {
            if (jsonNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                return convertObjNodeToParams(objectNode);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String convertObjNodeToParams(Object obj) {
        try {
            if (Objects.isNull(obj)) {
                return "";
            }
            JsonNode node = convertDtoToJson(obj);
            return convertObjNodeToParams(node);
        } catch (Exception e) {
            return "";
        }
    }

    public String convertObjNodeToParamsMultiParam(Object obj) {
        try {
            if (Objects.isNull(obj)) {
                return "";
            }
            JsonNode node = convertDtoToJson(obj);
            return convertObjNodeToParamsMultiParam(node);
        } catch (Exception e) {
            return "";
        }
    }

    private String convertObjNodeToParamsMultiParam(JsonNode jsonNode) {
        try {
            if (jsonNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                return convertObjNodeToParamsMultiParam(objectNode);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public JsonNode convertUrlParams2ObjNode(String url) {
        try {
            if (StringUtils.isEmpty(url)) {
                return null;
            }
            MultiValueMap<String, String> paramMap = UriComponentsBuilder.fromUriString(url).build().getQueryParams();
            Map<String, Object> paramMapAsJson = paramMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            entry -> entry.getValue().size() == 1 ? entry.getValue().get(0) : entry.getValue()));
            return convertStrToJson(objectMapper.writeValueAsString(paramMapAsJson));
        } catch (Exception e) {
            return null;
        }
    }

    public <T> JsonNode convertDtoToJson(T dto) {
        try {
            return new ObjectMapper().valueToTree(dto);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T getKeyJson(JsonNode jsonNode, Class<T> clazz, String... keys) {
        if (Objects.isNull(jsonNode) || keys == null || clazz == null) {
            return null;
        }
        jsonNode = dataInJson(jsonNode, Arrays.copyOfRange(keys, 0, keys.length - 1));
        String key = keys[keys.length - 1];
        if (Objects.nonNull(jsonNode) && jsonNode.has(key) && !jsonNode.get(key).isNull()) {
            if (clazz == String.class) {
                return clazz.cast(jsonNode.get(key).asText());
            } else if (clazz == Integer.class) {
                return clazz.cast(jsonNode.get(key).asInt());
            } else if (clazz == Double.class) {
                return clazz.cast(jsonNode.get(key).asDouble());
            } else if (clazz == Long.class) {
                return clazz.cast(jsonNode.get(key).asLong());
            } else if (clazz == Boolean.class) {
                return clazz.cast(jsonNode.get(key).asBoolean());
            }
            // Unsupported type
            throw new IllegalArgumentException("Unsupported target class: " + clazz.getName());
        }
        return null;
    }

    public <T> JsonNode sortJsonNodeList(JsonNode jsonNode, Class<T> clazz, String sortKey) {
        if (Objects.isNull(jsonNode) || sortKey == null || clazz == null) {
            return jsonNode;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        if (jsonNode.isArray()) {
            List<JsonNode> nodeList = new ArrayList<>();
            jsonNode.forEach(nodeList::add);
            if (clazz == Double.class) {
                nodeList.sort(Comparator.comparingDouble(node -> node.get(sortKey).asDouble()));
            }
            ArrayNode sortedArray = objectMapper.createArrayNode();
            nodeList.forEach(sortedArray::add);
            jsonNode = sortedArray;
        }
        return jsonNode;
    }

    public void putKeyJson(JsonNode jsonNode, String value, String... keys) {
        if (Objects.isNull(jsonNode) || keys == null) {
            return;
        }

        JsonNode keyNode = dataInJson(jsonNode, Arrays.copyOfRange(keys, 0, keys.length - 1));
        String key = keys[keys.length - 1];
        if (Objects.nonNull(keyNode) && keyNode instanceof ObjectNode) {
            ObjectNode node = (ObjectNode) keyNode;
            node.put(key, value);
        }
    }

    public void putKeyJson(JsonNode jsonNode, Boolean value, String... keys) {
        if (Objects.isNull(jsonNode) || keys == null) {
            return;
        }

        JsonNode keyNode = dataInJson(jsonNode, Arrays.copyOfRange(keys, 0, keys.length - 1));
        String key = keys[keys.length - 1];
        if (Objects.nonNull(keyNode) && keyNode instanceof ObjectNode) {
            ObjectNode node = (ObjectNode) keyNode;
            node.put(key, value);
        }
    }

    public void putKeyJson(JsonNode jsonNode, JsonNode value, String... keys) {
        if (Objects.isNull(jsonNode) || keys == null) {
            return;
        }

        JsonNode keyNode = dataInJson(jsonNode, Arrays.copyOfRange(keys, 0, keys.length - 1));
        String key = keys[keys.length - 1];
        if (Objects.nonNull(keyNode) && keyNode instanceof ObjectNode) {
            ObjectNode node = (ObjectNode) keyNode;
            node.set(key, value);
        }
    }

    public void putKeyJson(JsonNode jsonNode, Integer value, String... keys) {
        if (Objects.isNull(jsonNode) || keys == null) {
            return;
        }

        JsonNode keyNode = dataInJson(jsonNode, Arrays.copyOfRange(keys, 0, keys.length - 1));
        String key = keys[keys.length - 1];
        if (Objects.nonNull(keyNode) && keyNode instanceof ObjectNode) {
            ObjectNode node = (ObjectNode) keyNode;
            node.put(key, value);
        }
    }

    public <T> T deleteKeyJson(ObjectNode objectNode, String key, Class<T> clazz) {
        if (Objects.isNull(objectNode) || key == null || clazz == null) {
            return null;
        }
        if (objectNode.has(key)) {
            if (clazz == String.class) {
                return clazz.cast(objectNode.remove(key).asText());
            } else if (clazz == Integer.class) {
                return clazz.cast(objectNode.get(key).asInt());
            } else if (clazz == Double.class) {
                return clazz.cast(objectNode.get(key).asDouble());
            }
            throw new IllegalArgumentException("Unsupported target class: " + clazz.getName());
        }
        return null;
    }

    public <T> T deleteKeyJson(JsonNode jsonNode, String key, Class<T> clazz) {
        if (Objects.isNull(jsonNode) || key == null || clazz == null) {
            return null;
        }
        if (jsonNode instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            return deleteKeyJson(objectNode, key, clazz);
        }
        return null;
    }

    public int extractNumFromStr(String inputString) {
        if (StringUtils.isAllBlank(inputString))
            return 0;
        inputString = removeSpecChar(inputString);
        StringBuilder numberBuilder = new StringBuilder();
        for (char character : inputString.toCharArray()) {
            if (Character.isDigit(character)) {
                numberBuilder.append(character);
            } else if (numberBuilder.length() > 0) {
                break;
            }
        }
        return numberBuilder.length() > 0 ? Integer.parseInt(numberBuilder.toString()) : 0;
    }

    public String removeSpecChar(String input) {
        if (StringUtils.isAllBlank(input))
            return StringUtils.EMPTY;
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    public <T> String convertLstToStr(Collection<T> lst, String separation) {
        if (CollectionUtils.isEmpty(lst) || StringUtils.isEmpty(separation))
            return StringUtils.EMPTY;
        return lst.stream().map(Object::toString).collect(Collectors.joining(separation));
    }

    public ObjectNode convertUrlQueryToJson(String queryString) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                objectNode.put(keyValue[0], keyValue[1]);
            }
        }
        return objectNode;
    }

    public <T> ObjectNode generateObjNode(Map<String, T> map, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode generateObj = objectMapper.createObjectNode();
        if (Objects.isNull(map))
            return generateObj;
        map.forEach((key, value) -> {
            if (clazz == String.class) {
                String val = (String) clazz.cast(value);
                generateObj.put(key, val);
            } else if (clazz == Integer.class) {
                Integer val = (Integer) clazz.cast(value);
                generateObj.put(key, val);
            } else if (clazz == Long.class) {
                Long val = (Long) clazz.cast(value);
                generateObj.put(key, val);
            }
        });
        return generateObj;
    }

    public ObjectNode generateObjNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode();
    }

    public ArrayNode generateArrayNode() {
        return new ObjectMapper().createArrayNode();
    }

    public <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        Stream<T> stream1 = Optional.ofNullable(list1).map(List::stream).orElseGet(Stream::empty);
        Stream<T> stream2 = Optional.ofNullable(list2).map(List::stream).orElseGet(Stream::empty);
        return Stream.concat(stream1, stream2).collect(Collectors.toList());
    }

    /**
     * check Format: hh:mm
     * 
     * @param input
     * @return
     */
    public boolean checkHHMM(String input) {
        if (StringUtils.isBlank(input))
            return false;
        String regex = "^\\d{2}:\\d{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public long convertMinsToMilli(int minutes) {
        return minutes * 60 * 1000;
    }

    public long convert2Milliseconds(LocalDateTime localDateTime, int num) {
        if (Objects.isNull(localDateTime)) {
            return 0;
        }
        long result = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        if (num > -1) {
            result = Long.parseLong(Long.toString(result).substring(0, num));
        }
        return result;
    }

    public Random getRandom() {
        return random;
    }

    public ResponseEntity<Object> badRequest(String key, MsgEnum messageEnum, String... msg) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, String> errors = new HashMap<>();
        String message = messageEnum.getMessage();
        if (msg.length > 0) {
            String[] value = new String[msg.length + 1];
            value[0] = key;
            for (int i = 0; i < msg.length; i++) {
                value[i + 1] = msg[i];
            }
            errors.put(key, String.format(message, (Object[]) value));
        } else {
            errors.put(key, String.format(message, key));
        }
        return ResponseEntity.ok(err(errors));
    }

    public ResponseEntity<Object> badRequest(String key, String msg) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Map<String, String> errors = new HashMap<>();
        errors.put(key, msg);
        return ResponseEntity.badRequest().body(err(errors));
    }

    public ResponseEntity<Object> badRequest(Object errors, String... message) {
        return ResponseEntity.badRequest().body(err(errors));
    }

    public ResponseEntity<Object> badRequestOk(Object errors, String... message) {
        return ResponseEntity.ok(errStatusTrue(errors));
    }

    public <T> List<T> createList(@SuppressWarnings("unchecked") T... elements) {
        return Arrays.asList(elements);
    }

    public <T> Set<T> createSet(@SuppressWarnings("unchecked") T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    public boolean checkTrue(Boolean value) {
        return (Objects.nonNull(value) && value) ? true : false;
    }

    public void addUrl(StringBuffer urlBuffer, String value) {
        if (Objects.nonNull(urlBuffer) && StringUtils.isNotBlank(value)) {
            urlBuffer.append(String.format("/%s", value));
        }
    }

    public List<Double> extractNumbers(String input) {
        if (StringUtils.isBlank(input)) {
            return Collections.emptyList();
        }
        List<Double> numbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            numbers.add(Double.parseDouble(matcher.group()));
        }
        return numbers;
    }

    public boolean isLinux() {
        if (StringUtils.isNotBlank(OS) && OS.toLowerCase().startsWith("linux")) {
            return true;
        }
        return false;
    }

    public JsonNode getJsonInList(JsonNode list, String key, String value) {
        try {
            if (Objects.nonNull(list) && list.isArray() && StringUtils.isNoneBlank(key)
                    && StringUtils.isNoneBlank(value)) {
                return StreamSupport.stream(list.spliterator(), false)
                        .filter(item -> item.get(key).asText().equalsIgnoreCase(value)).findFirst().orElse(null);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public int random(int a, int b) {
        return random.nextInt(b - a + 1) + a;
    }

    public boolean enumError(FieldError error, String... field) {
        if (Objects.nonNull(field)) {
            for (String f : field) {
                if (f.equals(error.getField()) && "typeMismatch".equals(error.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String generateRandomHexString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(HEX_CHARACTERS.length());
            sb.append(HEX_CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public boolean isBase64(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        if (!str.matches("^[A-Za-z0-9+/=]+$")) {
            return false;
        }

        if (str.length() % 4 != 0) {
            return false;
        }
        return true;
    }

    public boolean isArrayNode(JsonNode jsonNode) {
        if (Objects.nonNull(jsonNode) && !jsonNode.isNull() && jsonNode.isArray()) {
            return true;
        }
        return false;
    }

    public boolean isNotEmpty(JsonNode jsonNode) {
        if (Objects.nonNull(jsonNode) && !jsonNode.isNull() && !jsonNode.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isEmpty(JsonNode jsonNode) {
        return !isNotEmpty(jsonNode);
    }

    public JsonNode nestedNode(JsonNode jsonNode, String... fieldNames) {
        if (Objects.isNull(jsonNode) || jsonNode.isEmpty()) {
            return jsonNode;
        }
        JsonNode currentNode = jsonNode;
        for (String fieldName : fieldNames) {
            if (currentNode == null) {
                return null;
            }
            currentNode = currentNode.get(fieldName);
        }
        return currentNode;
    }

    public JsonNode convertYamlToJson(String yamlString) throws JsonProcessingException {
        if (StringUtils.isEmpty(yamlString)) {
            return null;
        }
        Yaml yaml = new Yaml();
        List<Map<String, Object>> list = yaml.load(yamlString);
        String jsonStr = objectMapper.writeValueAsString(list);
        return convertStrToJson(jsonStr);
    }

    public String getBaseUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return StringUtils.EMPTY;
        }
        return UriComponentsBuilder.fromUriString(url).replaceQuery(null).build().toUriString();
    }

    public String findPatternUrl(String url, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(url);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean isPersistedQueryNotSupported(ResponseEntity<JsonNode> response) {
        if (Objects.isNull(response)) {
            return false;
        }
        JsonNode result = response.getBody();
        if (Objects.isNull(result) || result.isEmpty()) {
            return false;
        }
        JsonNode errors = dataInJson(result, "errors");
        if (Objects.nonNull(errors)) {
            for (JsonNode error : errors) {
                String message = getKeyJson(error, String.class, "message");
                if ("PersistedQueryNotSupported".equalsIgnoreCase(message)
                        || "PersistedQueryNotFound".equalsIgnoreCase(message)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Document convertContent2Document(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        return Jsoup.parse(content);
    }

    public JsonNode documentContains(String content, String contains) {
        Document document = this.convertContent2Document(content);
        if (Objects.nonNull(document)) {
            Elements scriptElements = document.getElementsByTag("script");
            if (Objects.nonNull(scriptElements)) {
                for (Element script : scriptElements) {
                    String scriptText = script.html();
                    if (scriptText.contains(contains)) {
                        return convertStrToJson(scriptText);
                    }
                }
            }
        }
        return null;
    }

    public JsonNode jsonNodeAt(JsonNode node, String at) {
        if (Objects.isNull(node) || node.isEmpty()) {
            return null;
        }
        try {
            return node.at(at);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T jsonNodeAt(JsonNode node, String at, Class<T> clazz) {
        if (node == null || node.isEmpty()) {
            return null;
        }
        try {
            JsonNode targetNode = node.at(at);
            if (!isEmpty(targetNode) || targetNode.isNull()) {
                return null;
            }

            if (clazz == String.class) {
                return clazz.cast(targetNode.asText());
            } else if (clazz == Integer.class) {
                return clazz.cast(targetNode.asInt());
            } else if (clazz == Long.class) {
                return clazz.cast(targetNode.asLong());
            } else if (clazz == Boolean.class) {
                return clazz.cast(targetNode.asBoolean());
            } else if (clazz == Double.class) {
                return clazz.cast(targetNode.asDouble());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.treeToValue(targetNode, clazz);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getMyIp() {
        try {
            return readTextFile("data_static/my_ip.txt");
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isValidURL(String url) {
        String urlRegex = "^(https?|ftp)://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(:\\d+)?(/.*)?$";
        Pattern pattern = Pattern.compile(urlRegex);
        return pattern.matcher(url).matches();
    }

    public String getXForwardedFor(Map<String, String> headers) {
        if (Objects.isNull(headers)) {
            return StringUtils.EMPTY;
        }
        String ip = headers.getOrDefault("x-forwarded-for", "").split(",")[0].trim();
        return ip;
    }

    public <T> List<T> ramdomList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
