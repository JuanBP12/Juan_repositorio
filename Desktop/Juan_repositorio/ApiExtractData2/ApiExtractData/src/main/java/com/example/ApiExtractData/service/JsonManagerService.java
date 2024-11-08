package com.example.ApiExtractData.service;

import com.example.ApiExtractData.assembler.*;
import com.example.ApiExtractData.dto.AttributeDTO;
import com.example.ApiExtractData.dto.AttributeTypeDTO;
import com.example.ApiExtractData.dto.AttributeTypeValueDTO;
import com.example.ApiExtractData.dto.ConfigDTO;
import com.example.ApiExtractData.model.Attribute;
import com.example.ApiExtractData.model.AttributeType;
import com.example.ApiExtractData.model.AttributeTypeValue;
import com.example.ApiExtractData.model.Config;
import com.example.ApiExtractData.repository.AttributeRepository;
import com.example.ApiExtractData.repository.AttributeTypeRepository;
import com.example.ApiExtractData.repository.AttributeTypeValueRepository;
import com.example.ApiExtractData.repository.ConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JsonManagerService {

    private final ConfigRepository configRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeTypeValueRepository attributeTypeValueRepository;
    private final ConfigAssembler configAssembler;
    private final AttributeAssembler attributeAssembler;
    private final AttributeTypeAssembler attributeTypeAssembler;
    private final AttributeTypeValueAssembler attributeTypeValueAssembler;

    ObjectMapper mapper = new CustomObjectMapper(); // Creamos una instancia del Mapper que hemos modificado

    public JsonManagerService(ConfigRepository configRepository, AttributeRepository attributeRepository, AttributeTypeRepository attributeTypeRepository, AttributeTypeValueRepository attributeTypeValueRepository, ConfigAssembler configAssembler, AttributeAssembler attributeAssembler, AttributeTypeAssembler attributeTypeAssembler, AttributeTypeValueAssembler attributeTypeValueAssembler) {
        this.configRepository = configRepository;
        this.attributeRepository = attributeRepository;
        this.attributeTypeRepository = attributeTypeRepository;
        this.attributeTypeValueRepository = attributeTypeValueRepository;
        this.configAssembler = configAssembler;
        this.attributeAssembler = attributeAssembler;
        this.attributeTypeAssembler = attributeTypeAssembler;
        this.attributeTypeValueAssembler = attributeTypeValueAssembler;
    }

    // metodo para serializar el objeto directamente desde la entidad
    public String serialize() throws IOException, JsonProcessingException {

        // Crear un mapa para contener todos los datos
        ObjectNode rootNode = mapper.createObjectNode();


        // Antes cogiamos los objetos de sus respectivos repositorios ahora solo cogemos el objeto raiz, de esta manera si hemos deserializado usando dtos al serializar directamente desde la entidad se muestran los objetos anidados a config formando un tree, gracias a las anotaciones @jsonbackreference y @jsonmanagerreference

        // si deserializamos desde la entidad directamente a la hora de serializar desde la entidad solo nos mostraria config, tendriamos que descomentar las lineas
        List<Config> configs = configRepository.findAll();
        //List<Attribute> attributes = attributeRepository.findAll();
        //List<AttributeType> attributeTypes = attributeTypeRepository.findAll();
        //List<AttributeTypeValue> attributeTypeValues = attributeTypeValueRepository.findAll();


        // Añadir los datos al nodo raíz
        rootNode.putPOJO("configs", configs);
        //rootNode.putPOJO("attributes", attributes);
        //rootNode.putPOJO("attributeTypes", attributeTypes);
        //rootNode.putPOJO("attributeTypeValues", attributeTypeValues);

        // Creamos un fichero donde se almacenaran los datos
        try {
            File fichero = new File("config.json");
            fichero.createNewFile();
            mapper.writeValue(fichero, rootNode); // Escribir en el archivo un solo llamado
            // Devolvemos el json generado por consola y al swagger
            System.out.println(mapper.writeValueAsString(rootNode));
            return mapper.writeValueAsString(rootNode); // Convierte el objeto a una cadena JSON
        } catch (JsonProcessingException d) {
            System.err.println("Error serializing data: " + d.getMessage());
            d.printStackTrace();
            throw d;
        } catch (IOException e) {
            System.err.println("Error serializing data: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    // metodo para serializar datos de los objetos en formato json pasandolos primero a dto, muestra los objetos separados pero incluyendo los ids de referencia
    public String serializeDTO() throws IOException, JsonProcessingException {
        ObjectNode rootNode = mapper.createObjectNode();

        try {
            // Recuperar todos los objetos Config del repositorio y los convierte a DTO
            List<ConfigDTO> configDTOs = configRepository.findAll().stream()// Obtiene los objetos config y los añade a una lista llamada config
                    .map(configAssembler::configToDTO)
                    .collect(Collectors.toList());

            List<AttributeDTO> attributeDTOs = attributeRepository.findAll().stream()
                    .map(attributeAssembler::attributeToDTO)
                    .collect(Collectors.toList());

            // Convertir todos los tipos de atributos a DTOs
            List<AttributeTypeDTO> attributeTypeDTOs = attributeTypeRepository.findAll().stream()
                    .map(attributeTypeAssembler::attributeTypeToDTO)
                    .collect(Collectors.toList());

            // Convertir todos los valores de tipos de atributos a DTOs
            List<AttributeTypeValueDTO> attributeTypeValueDTOs = attributeTypeValueRepository.findAll().stream()
                    .map(attributeTypeValueAssembler::attributeTypeValueToDTO)
                    .collect(Collectors.toList());

            // Añadir los DTOs al nodo JSON
            rootNode.putPOJO("attributes", attributeDTOs);
            rootNode.putPOJO("configs", configDTOs);
            rootNode.putPOJO("attributeTypes", attributeTypeDTOs);
            rootNode.putPOJO("attributeTypeValues", attributeTypeValueDTOs);

            //Escribir el fichero en un archivo
            File fichero = new File("configDTO.json");
            fichero.createNewFile();
            mapper.writeValue(fichero, rootNode); // Escribir en el archivo
            //devolvemos el json generado por consola y al swagger
            System.out.println(mapper.writeValueAsString(rootNode));
            return mapper.writeValueAsString(rootNode); // Convierte el objeto a una cadena JSON
        } catch (JsonProcessingException d) {
            System.err.println("Error serializing data: " + d.getMessage());
            d.printStackTrace();
            throw d;
        } catch (IOException e) {
            System.err.println("Error serializing data: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // metodo deserializar datos del json en un objeto
    public void deserialize(String json) throws IOException {
        // Crear instancia de ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Leer el JSON y convertirlo en un JsonNode
            JsonNode rootNode = mapper.readTree(json);

            // Iterar sobre el primer nivel (configuraciones principales)
            Iterator<Map.Entry<String, JsonNode>> rootFields = rootNode.fields();
            while (rootFields.hasNext()) {
                Map.Entry<String, JsonNode> field = rootFields.next();
                String configKey = field.getKey(); // Configuración del primer nivel
                JsonNode configNode = field.getValue(); // Nodo de configuración que contiene datos anidados
                JsonNodeType nodeType = configNode.getNodeType();
                System.out.println("Config node es un: " + nodeType);


                // Crear y configurar el objeto Config
                Config config = new Config();
                config.setDescription("Example"); // Asignar configKey como descripción
                config.setIsCustom(true);  // Valor por defecto o ajustar según necesites
                config.setDefaultValue("default");  // Ajustar según el JSON real
                config.setApplicationNode(configKey);  // Ajustar según el JSON real
                config.setParent(0L);  // Ajustar según el JSON real

                System.out.println("Config Key: " + configKey);
                configRepository.save(config);

                switch (nodeType) {
                    case STRING:
                        System.out.println("El valor es: " + configNode.textValue());

                        AttributeType attributeType = new AttributeType();
                        attributeType.setType("String");
                        attributeType.setEnumDescription("enumDesc");
                        attributeType.setIsEnum(false);
                        attributeType.setIsList(false);
                        attributeTypeRepository.save(attributeType);


                        AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                        attributeTypeValue.setDescription("Example");
                        attributeTypeValue.setValue(configNode.textValue());
                        attributeTypeValueRepository.save(attributeTypeValue);
                        break;
                    case OBJECT:
                        // Cuando el valor es un objeto, debemos recorrer sus campos
                        processObjectNode(configNode);
                        break;
                    default:
                        // Si el nodo no es un STRING ni un OBJECT, se podría manejar como un tipo desconocido
                        System.out.println("Nodo de tipo desconocido: " + nodeType);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error deserializing JSON: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Metodo recursivo para manejar objetos anidados
    private void processObjectNode(JsonNode node) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> attributeEntry = fields.next();
            String attributeKey = attributeEntry.getKey();
            JsonNode attributeValueNode = attributeEntry.getValue();
            JsonNodeType nodeType = attributeValueNode.getNodeType();

            System.out.println("attributeKey: " + attributeKey);

            // Guardar atributo
            Attribute attribute = new Attribute();
            attribute.setName(attributeKey);
            attributeRepository.save(attribute);

            // Procesar el tipo de dato del valor
            if (nodeType == JsonNodeType.STRING) {
                System.out.println("attributeValue: " + attributeValueNode.textValue());
                AttributeType attributeType = new AttributeType();
                attributeType.setType("String");
                attributeType.setEnumDescription("enumDesc");
                attributeType.setIsEnum(false);
                attributeType.setIsList(false);
                attributeTypeRepository.save(attributeType);

                AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                attributeTypeValue.setDescription("Example");
                attributeTypeValue.setValue(attributeValueNode.textValue());
                attributeTypeValueRepository.save(attributeTypeValue);

            } else if (nodeType == JsonNodeType.NUMBER) {
                System.out.println("attributeValue: " + attributeValueNode.numberValue().toString());
                AttributeType attributeType = new AttributeType();
                attributeType.setType("Number");
                attributeType.setEnumDescription("enumDesc");
                attributeType.setIsEnum(false);
                attributeType.setIsList(false);
                attributeTypeRepository.save(attributeType);

                AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                attributeTypeValue.setDescription("Example");
                attributeTypeValue.setValue(String.valueOf(attributeValueNode.booleanValue()));
                attributeTypeValueRepository.save(attributeTypeValue);

            }else if (nodeType == JsonNodeType.BOOLEAN) {
                System.out.println("attributeValue: " + attributeValueNode.textValue());
                AttributeType attributeType = new AttributeType();
                attributeType.setType("Boolean");
                attributeType.setEnumDescription("enumDesc");
                attributeType.setIsEnum(false);
                attributeType.setIsList(false);
                attributeTypeRepository.save(attributeType);

                AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                attributeTypeValue.setDescription("Example");
                attributeTypeValue.setValue(String.valueOf(attributeValueNode.booleanValue()));
                attributeTypeValueRepository.save(attributeTypeValue);
            }else if (nodeType == JsonNodeType.OBJECT) {
                // Si el valor es otro objeto, procesarlo recursivamente
                processObjectNode(attributeValueNode);
            }
        }
    }


    // metodo deserializar datos del json en objeto usando dtos
    public void deserializeDTO(String json) throws IOException {
        // Crear instancia de ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Leer el JSON y convertirlo en un JsonNode
            JsonNode rootNode = mapper.readTree(json);

            // Iterar sobre el primer nivel (configuraciones principales)
            Iterator<Map.Entry<String, JsonNode>> rootFields = rootNode.fields();
            while (rootFields.hasNext()) {
                Map.Entry<String, JsonNode> field = rootFields.next();
                String configKey = field.getKey(); // Configuración del primer nivel
                JsonNode configNode = field.getValue(); // Nodo de configuración que contiene datos anidados
                JsonNodeType nodeType = configNode.getNodeType();
                System.out.println("Config node es un: " + nodeType);

                // Crear el DTO ConfigDTO y asignar valores predeterminados
                ConfigDTO configDTO = new ConfigDTO();
                configDTO.setDescription("Example"); // Asignar configKey como descripción
                configDTO.setIsCustom(true);  // Valor por defecto o ajustar según necesites
                configDTO.setDefaultValue("default");  // Ajustar según el JSON real
                configDTO.setApplicationNode(configKey);  // Ajustar según el JSON real
                configDTO.setParent(0L);  // Ajustar según el JSON real
                List<Long> attributeId = new ArrayList<>();
                attributeId.add(1L);
                attributeId.add(2L);
                attributeId.add(3L);
                configDTO.setAttributeIds(attributeId);

                System.out.println("Config Key: " + configKey);
                // Ahora convertimos el DTO en entidad y la guardamos
                ConfigAssembler configAssembler = new ConfigAssembler(attributeRepository, configRepository);
                Config configEntity = configAssembler.configToEntity(configDTO);
                configRepository.save(configEntity);

                // Procesamos el tipo de dato basado en el nodo
                switch (nodeType) {
                    case STRING:
                        System.out.println("El valor es: " + configNode.textValue());

                        // Crear y guardar el AttributeType y AttributeTypeValue
                        AttributeType attributeType = new AttributeType();
                        attributeType.setType("String");
                        attributeType.setEnumDescription("enumDesc");
                        attributeType.setIsEnum(false);
                        attributeType.setIsList(false);
                        attributeTypeRepository.save(attributeType);

                        AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                        attributeTypeValue.setDescription("Example");
                        attributeTypeValue.setValue(configNode.textValue());
                        attributeTypeValueRepository.save(attributeTypeValue);
                        break;
                    case OBJECT:
                        // Cuando el valor es un objeto, debemos recorrer sus campos
                        processObjectNodeDTO(configNode);
                        break;
                    default:
                        // Si el nodo no es un STRING ni un OBJECT, se podría manejar como un tipo desconocido
                        System.out.println("Nodo de tipo desconocido: " + nodeType);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error deserializing JSON: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Metodo recursivo para manejar objetos anidados en DTOs
    private void processObjectNodeDTO(JsonNode node) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> attributeEntry = fields.next();
            String attributeKey = attributeEntry.getKey();
            JsonNode attributeValueNode = attributeEntry.getValue();
            JsonNodeType nodeType = attributeValueNode.getNodeType();

            System.out.println("attributeKey: " + attributeKey);

            // Convertir el atributo en un DTO y guardarlo
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setName(attributeKey);
            // Aquí puedes llenar los demás campos del DTO según los valores que recibas
            AttributeAssembler attributeAssembler = new AttributeAssembler(configRepository, attributeTypeRepository, attributeRepository);
            Attribute attributeEntity = attributeAssembler.attributeToEntity(attributeDTO);
            attributeRepository.save(attributeEntity);

            // Procesar el tipo de dato del valor
            if (nodeType == JsonNodeType.STRING) {
                System.out.println("attributeValue: " + attributeValueNode.textValue());

                // Convertir y guardar el AttributeType y AttributeTypeValue
                AttributeType attributeType = new AttributeType();
                attributeType.setType("String");
                attributeType.setEnumDescription("enumDesc");
                attributeType.setIsEnum(false);
                attributeType.setIsList(false);
                attributeTypeRepository.save(attributeType);

                AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                attributeTypeValue.setDescription("Example");
                attributeTypeValue.setValue(attributeValueNode.textValue());
                attributeTypeValueRepository.save(attributeTypeValue);

            } else if (nodeType == JsonNodeType.NUMBER) {
                System.out.println("attributeValue: " + attributeValueNode.numberValue().toString());
                AttributeType attributeType = new AttributeType();
                attributeType.setType("Number");
                attributeType.setEnumDescription("enumDesc");
                attributeType.setIsEnum(false);
                attributeType.setIsList(false);
                attributeTypeRepository.save(attributeType);

                AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                attributeTypeValue.setDescription("Example");
                attributeTypeValue.setValue(attributeValueNode.numberValue().toString());
                attributeTypeValueRepository.save(attributeTypeValue);

            }else if (nodeType == JsonNodeType.BOOLEAN) {
                System.out.println("attributeValue: " + attributeValueNode.booleanValue());
                AttributeType attributeType = new AttributeType();
                attributeType.setType("Boolean");
                attributeType.setEnumDescription("enumDesc");
                attributeType.setIsEnum(false);
                attributeType.setIsList(false);
                attributeTypeRepository.save(attributeType);

                AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                attributeTypeValue.setDescription("Example");
                attributeTypeValue.setValue(String.valueOf(attributeValueNode.booleanValue()));
                attributeTypeValueRepository.save(attributeTypeValue);
            }else if (nodeType == JsonNodeType.OBJECT) {
                // Si el valor es otro objeto, procesarlo recursivamente
                processObjectNodeDTO(attributeValueNode);
            }
        }
    }
}