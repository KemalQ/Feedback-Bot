# Feedback Processing System (Telegram Bot + AI + Automation)

Backend system for collecting and processing anonymous employee feedback with AI-based analysis and automated workflows.

## 🚀 Overview

This project implements a distributed feedback processing system using event-driven architecture.

Users submit anonymous feedback via Telegram bot. Messages are asynchronously processed, analyzed using LLM (Groq API), and automatically routed into external systems for tracking and resolution.

---

## ⚙️ Key Features

- Anonymous feedback collection via Telegram Bot
- Role-based onboarding (mechanic, electrician, manager)
- Branch selection (multi-location support)
- AI-powered message analysis:
    - sentiment detection (positive / neutral / negative)
    - criticality scoring (1–5)
    - suggested resolution
- Automated workflows:
    - logging to Google Sheets
    - Trello card creation for critical issues (level 4–5)
- Asynchronous message processing via RabbitMQ
- Multi-module backend architecture

---

## 🏗 Architecture

System is built using **event-driven architecture**:

- Telegram Bot → receives messages
- Producer → publishes events to RabbitMQ
- Consumer → processes messages asynchronously
- External integrations:
    - Groq API (Llama processing)
    - Google Sheets API
    - Trello API

### Messaging

- RabbitMQ used for decoupled communication
- Configured exchanges:
    - Direct
    - Fanout
    - Topic
- Implemented:
    - message acknowledgment (ack/nack)
    - retry mechanism for failed processing

---

## 🧱 Tech Stack

- Java 21
- Spring Boot
- PostgreSQL
- RabbitMQ
- Docker
- Telegram Bot API
- Groq API (Llama)
- Google Sheets API
- Trello API

---

## 🐳 Running the Project

### Requirements

- Docker
- Docker Compose

### Start

```bash
docker-compose up --build