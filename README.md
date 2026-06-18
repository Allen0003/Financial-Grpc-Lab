Markdown

# Financial-Grpc-Lab

A lightweight, enterprise-grade gRPC hands-on laboratory demonstrating high-performance RPC communication within a Spring Boot environment. This project serves as a foundational step toward understanding Cloud-Native Microservices architecture, Protocol Buffers (Protobuf), and L7 load balancing.

## 🏗️ Project Architecture

This repository is structured as a Maven Multi-Module project to properly decouple contract definitions from business logic:


Financial-Grpc-Lab
├── pom.xml (Parent POM managing global dependencies & versions)
├── grpc-api
│   ├── pom.xml (Handles Protobuf compilation via protobuf-maven-plugin)
│   └── src/main/proto
│       └── order.proto (The single source of truth / API contract)
├── grpc-server
│   ├── pom.xml (Implements the gRPC business logic & hosts the gRPC Server)
│   └── src (Java Source)
└── grpc-client
    ├── pom.xml (Acts as a gRPC Client invoking remote stubs via channels)
    └── src (Java Source)

🛠️ Tech Stack & Prerequisites

    Java Java 17 (Source/Target compatibility)

    Spring Boot 3.2.4

    gRPC Spring Boot Starter 3.1.0.RELEASE

    Protocol Buffers v3 (proto3)

    Apache Maven

🚀 Getting Started
1. Project Directory Verification

Ensure your .proto file is placed exactly under the following path conforming to Maven conventions:
Bash

grpc-api/src/main/proto/order.proto

2. Compile and Generate Stubs

Run the following command at the root directory (Financial-Grpc-Lab) to trigger the protobuf-maven-plugin. This compilation process automatically generates Java source codes (Stubs, Request/Response DTOs) from your IDL contract:
Bash

mvn clean compile

After a successful build, you can inspect the generated Java artifacts under:
Bash

grpc-api/target/generated-sources/protobuf/

📄 API Contract (order.proto)

The interface definition defines an asynchronous-ready binary protocol for order processing:
Protocol Buffers

syntax = "proto3";

option java_multiple_files = true;
option java_package = "com.example.grpc.order";
option java_outer_classname = "OrderProto";

service OrderService {
  rpc GetOrder (OrderRequest) returns (OrderResponse);
}

message OrderRequest {
  string order_id = 1;
}

message OrderResponse {
  string order_id = 1;
  string product_name = 2;
  double price = 3;
  string status = 4;
}

