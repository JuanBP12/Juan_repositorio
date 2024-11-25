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

    // Metodo para serializar el objeto directamente desde la entidad
    public String serialize() throws IOException, JsonProcessingException {

        // Crear un mapa para contener todos los datos
        ObjectNode rootNode = mapper.createObjectNode();

        // Recuperar los objetos Config directamente del repositorio
        List<Config> configs = configRepository.findAll();

        // Recorrer los objetos Config para construir la jerarquía anidada
        for (Config config : configs) {
            // Usar recursividad para agregar los datos anidados
            if (config.getParent() == null) { // Solo procesamos los nodos raíz
                // Agregar la jerarquía desde el nodo raíz
                rootNode.set(config.getApplicationNode(), buildConfigTree(config));
            }
        }

        // Crear un fichero donde se almacenarán los datos en formato JSON
        try {
            File fichero = new File("config.json");
            fichero.createNewFile();
            mapper.writeValue(fichero, rootNode); // Escribir el JSON en el archivo
            // Devolvemos el JSON generado por consola y al swagger
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

    private ObjectNode buildConfigTree(Config config) {
        // Crear un nodo para la configuración actual
        ObjectNode configNode = mapper.createObjectNode();

        // Recuperar los atributos de la configuración
        List<Attribute> attributes = config.getAttributes();

        // Recorrer los atributos y construir la jerarquía
        for (Attribute attribute : attributes) {
            // Obtener el tipo de atributo relacionado
            AttributeType attributeType = attribute.getAttributeType();
            String attributeName = attribute.getName();

            // Procesar los valores asociados a este tipo de atributo, si los hay
            List<AttributeTypeValue> attributeTypeValues = attributeType.getAttributeTypeValues();
            if (attributeTypeValues != null && !attributeTypeValues.isEmpty()) {
                for (AttributeTypeValue attributeTypeValue : attributeTypeValues) {
                    configNode.put(attributeName, attributeTypeValue.getValue());
                }
            }
        }

        // Si el nodo tiene hijos, agregar los hijos de forma recursiva
        if (config.getChildren() != null && !config.getChildren().isEmpty()) {
            for (Config child : config.getChildren()) {
                // Llamar recursivamente para cada hijo, manteniendo la estructura de jerarquía
                configNode.set(child.getApplicationNode(), buildConfigTree(child));
            }
        }

        return configNode;
    }



    // metodo para serializar el objeto directamente desde la entidad
    public String serialize2() throws IOException, JsonProcessingException {

        // Crear un mapa para contener todos los datos
        ObjectNode rootNode = mapper.createObjectNode();


        // Antes cogiamos los objetos de sus respectivos repositorios ahora solo cogemos el objeto raiz, de esta manera si hemos deserializado usando dtos al serializar directamente desde la entidad se muestran los objetos anidados a config formando un tree, gracias a las anotaciones @jsonbackreference y @jsonmanagerreference

        // si deserializamos desde la entidad directamente a la hora de serializar desde la entidad solo nos mostraria config, tendriamos que descomentar las lineas
        // Recuperar los objetos Config directamente del repositorio
        List<Config> configs = configRepository.findAll();
        //List<Attribute> attributes = attributeRepository.findAll();
        //List<AttributeType> attributeTypes = attributeTypeRepository.findAll();
        //List<AttributeTypeValue> attributeTypeValues = attributeTypeValueRepository.findAll();


        // Agregar los objetos recuperados al nodo JSON raíz
        rootNode.putPOJO("configs", configs);
        //rootNode.putPOJO("attributes", attributes);
        //rootNode.putPOJO("attributeTypes", attributeTypes);
        //rootNode.putPOJO("attributeTypeValues", attributeTypeValues);

        // Creamos un fichero donde se almacenaran los datos en formato JSON
        try {
            File fichero = new File("config2.json");
            fichero.createNewFile();
            mapper.writeValue(fichero, rootNode); // Escribir el JSON en el archivo
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

            // Creamos un fichero donde se almacenaran los datos en formato JSON
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
                JsonNodeType nodeType = configNode.getNodeType(); // Almacenamos el tipo de nodo

                // Crear y configurar el objeto Config
                Config config = new Config();
                config.setDescription("Example"); // Asignar configKey como descripción
                config.setIsCustom(true);  // Valor por defecto o ajustar según necesites
                config.setDefaultValue("default");
                config.setApplicationNode(configKey);  // añadimos el nombre de la configuracion
                config.setParent(null); // ponemos el parent principal a null
                Config savedConfig = configRepository.save(config); // guardamos la config y la almacenamos en una variable

                switch (nodeType) {
                    case STRING:
                    case NUMBER:
                    case BOOLEAN:

                        // Crear y asociar el atributo para un tipo STRING
                        Attribute attribute = new Attribute();
                        attribute.setName(configKey);  // Asignar el nombre del atributo
                        attribute.setConfig(savedConfig);  // Asignar la configuración a este atributo

                        // Crear el AttributeType y asociarlo
                        AttributeType attributeType = new AttributeType();
                        attributeType.setType(nodeType.name());
                        attributeType.setEnumDescription("enumDesc");
                        attributeType.setIsEnum(false);
                        attributeType.setIsList(false);
                        AttributeType savedAttributeType = attributeTypeRepository.save(attributeType);

                        // Asociar el AttributeType al atributo
                        attribute.setAttributeType(savedAttributeType);
                        attributeRepository.save(attribute);  // Guardar el atributo

                        // Crear el valor del atributo (AttributeTypeValue) y asociarlo
                        AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
                        attributeTypeValue.setDescription("Example");
                        attributeTypeValue.setValue(configNode.textValue());
                        attributeTypeValue.setAttributeType(savedAttributeType);
                        attributeTypeValueRepository.save(attributeTypeValue);

                        break;
                    case OBJECT:
                        // Cuando el valor es un objeto, debemos recorrer sus campos
                        processObjectNode(configNode, savedConfig); // llamamos al metodo de forma recursiva
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
    private void processObjectNode(JsonNode node, Config parentConfig) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String configOrAttributeKey = entry.getKey();
            JsonNode configOrAttributeValueNode = entry.getValue();
            JsonNodeType nodeType = configOrAttributeValueNode.getNodeType();

            Config childConfig = null; // Inicializar childConfig aquí, para usarlo en el bloque recursivo

            if (nodeType == JsonNodeType.OBJECT) {
                // Guardar config
                childConfig = new Config();
                childConfig.setDescription("Example"); // Asignar configKey como descripción
                childConfig.setIsCustom(true);  // Valor por defecto o ajustar según necesites
                childConfig.setDefaultValue("default");  // Ajustar según el JSON real
                childConfig.setApplicationNode(configOrAttributeKey);  // Ajustar según el JSON real
                childConfig.setParentId(parentConfig.getId());
                childConfig = configRepository.save(childConfig); // Guardamos el childConfig
            } else {
                // Guardar atributo
                Attribute attribute = new Attribute();
                attribute.setName(configOrAttributeKey);
                attribute.setConfig(parentConfig); // Asignamos parentConfig por defecto

                // Aquí se asigna un AttributeType adecuado basado en el tipo de valor
                createAttributeTypeAndValue(configOrAttributeValueNode, nodeType.name(), attribute);

                attributeRepository.save(attribute);
            }

            // Después de manejar el nodo actual, si es otro objeto, procesarlo recursivamente
            if (nodeType == JsonNodeType.OBJECT) {
                // Llamada recursiva: pasamos childConfig en lugar de parentConfig
                processObjectNode(configOrAttributeValueNode, childConfig);
            }
        }
    }


    private void createAttributeTypeAndValue(JsonNode valueNode, String type, Attribute attribute) {
        AttributeType attributeType = new AttributeType();
        attributeType.setType(type);
        attributeType.setEnumDescription("enumDesc");
        attributeType.setIsEnum(false);
        attributeType.setIsList(false);
        AttributeType savedAttributeType = attributeTypeRepository.save(attributeType);

        // Asociar el AttributeType al Attribute
        attribute.setAttributeType(savedAttributeType); // Asignamos el AttributeType al Attribute

        AttributeTypeValue attributeTypeValue = new AttributeTypeValue();
        attributeTypeValue.setDescription("Example");
        attributeTypeValue.setValue(valueNode.asText());
        attributeTypeValue.setAttributeType(savedAttributeType);
        attributeTypeValueRepository.save(attributeTypeValue);
    }
}

