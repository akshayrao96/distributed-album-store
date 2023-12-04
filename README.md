# Album Store System with RabbitMQ Integration

## Overview
This project utilizes RabbitMQ as a message broker to efficiently handle database updates for an album store system. The system processes likes and dislikes on albums using a multi-threaded client-server model.

## Program Flow

### Client
- **Actions**: Posts an album, followed by likes and dislikes.
- **Threads**: Executes 30 threads, each repeating the posting process 100 times.
- **Outcome**: Results in 30,000 albums written to DynamoDB, each with 2 likes and 1 dislike.

### Server
- **Components**: Retains all classes from previous assignments, with the addition of `ReviewServlet` and RabbitMQ configurations.
- **ReviewServlet**: Receives review actions (`like/dislike`) and utilizes `RabbitMQProducer` for publishing messages to RabbitMQ.

### RabbitMQ Integration
- **Producer**: Batches messages in RAM, publishing them every 5 seconds to reduce load and enhance performance.
- **Consumer**: Multiple instances configured for direct message consumption from the queue, with auto-acknowledgement for increased efficiency.

### Notes
- The RabbitMQ setup focuses on reducing network latency.
- Batch asynchronous publishing and a multiple consumer strategy were adopted for performance optimization, max 1 consumer per message to avoid race conditions.
- The system is designed for simplicity and high throughput, with a trade-off in transactional guarantees.

## Maven Commands

### AlbumStoreClient
cd ./client/AlbumStoreClient
mvn clean install

### AlbumStoreServer
cd ./server/AlbumStoreServer
mvn clean install
