package com.example.server

import com.alibaba.fastjson.annotation.JSONField


object ServerConfig {
    @JSONField(ordinal = 1)
    var netmask: String = "255.255.255.0"

    @JSONField(ordinal = 2)
    var unicast: String = "127.0.0.1"

    @JSONField(ordinal = 3)
    var applications: List<Application> = listOf(
        Application("Server", "0x1234")
    )

    @JSONField(ordinal = 4)
    var services: List<Service> = listOf(
        Service(
            "0x1234",
            "0x5678",
            "30509"
        )
    )

    @JSONField(ordinal = 5)
    var routing: String = "Server"

    @JSONField(name = "service-discovery", ordinal = 6)
    var service_discovery: ServiceDiscovery = ServiceDiscovery(
        "true",
        "224.224.224.245",
        "30500",
        "udp",
        "10",
        "100",
        "200",
        "3",
        "3",
        "2000",
        "1500"
    )
}

data class Application(
    var name: String,
    var id: String
)

data class Service(
    @JSONField(ordinal = 1)
    var service: String,
    @JSONField(ordinal = 2)
    var instance: String,
    @JSONField(ordinal = 3)
    var unreliable: String
)

data class ServiceDiscovery(
    var enable: String,
    var multicast: String,
    var port: String,
    var protocol: String,
    var initial_delay_min: String,
    var initial_delay_max: String,
    var repetitions_base_delay: String,
    var repetitions_max: String,
    var ttl: String,
    var cyclic_offer_delay: String,
    var request_response_delay: String
)

