package com.wildcats.project;

import com.google.gson.Gson;
import com.wildcats.project.entity.DeviceData;
import com.wildcats.project.entity.Dispenser;
import com.wildcats.project.request.DeviceDataRequest;
import com.wildcats.project.request.DispenserRequest;
import com.wildcats.project.service.DeviceDataService;
import com.wildcats.project.service.DispenserService;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.RequiredArgsConstructor;
import lombok.var;
import net.minidev.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@RequiredArgsConstructor
public class MqqtBeans {

    private final DispenserService dispenserService;
    private final DeviceDataService deviceDataService;

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[] {"tcp://mqtt.eclipseprojects.io:1883"});
        options.setCleanSession(true);

        factory.setConnectionOptions(options);

        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
       return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("Id_9090271", mqttPahoClientFactory(),
                "sensor/+/data/#");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();

                var msg = message.getPayload();
                String text =  msg.toString();

                Gson gson = new Gson();
                DispenserRequest objdispenser = gson.fromJson(text, DispenserRequest.class);

                Dispenser macAddress = dispenserService.getByMacAddress(objdispenser.getMacAddress());

                System.out.println(objdispenser);
                if (objdispenser != null) {
                    if (macAddress != null) {
                        Dispenser dispenser = dispenserService.update(macAddress.getId(), objdispenser);
                        //System.out.println("pai" + dispenser);
                        DeviceDataRequest deviceDataRequest = new DeviceDataRequest();
                        deviceDataRequest.setIdDispenser(dispenser.getId());
                        deviceDataRequest.setFluidLevel(dispenser.getFluidLevel());
                        deviceDataRequest.setLocal(dispenser.getLocal());
                        deviceDataRequest.setUsed(dispenser.getUsed());
                        deviceDataRequest.setStocked(objdispenser.isStocked());
                        deviceDataRequest.setAllUsedCount(dispenser.getAllUsedCount());
                        deviceDataRequest.setUpdatedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                        DeviceData deviceData = deviceDataService.salvar(deviceDataRequest);
                        //System.out.println(deviceData);
                    } else {
                        Dispenser dispenser = dispenserService.salvar(objdispenser);
                        //System.out.println("mae" + dispenser);
                        DeviceDataRequest deviceDataRequest = new DeviceDataRequest();
                        deviceDataRequest.setIdDispenser(dispenser.getId());
                        deviceDataRequest.setFluidLevel(dispenser.getFluidLevel());
                        deviceDataRequest.setLocal(dispenser.getLocal());
                        deviceDataRequest.setUsed(dispenser.getUsed());
                        deviceDataRequest.setStocked(objdispenser.isStocked());
                        deviceDataRequest.setAllUsedCount(dispenser.getAllUsedCount());
                        deviceDataRequest.setUpdatedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                        DeviceData deviceData = deviceDataService.salvar(deviceDataRequest);
                        //System.out.println(deviceData);
                    }


                }
            }
        };
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("ServerOut", mqttPahoClientFactory());

        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("sensor/+/data/#");
        return messageHandler;
    }

}
