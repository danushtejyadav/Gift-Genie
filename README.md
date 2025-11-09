# Gift Genie 🎁✨

An intelligent chatbot that helps you find the perfect gift by asking insightful, psychological questions about the recipient.


[Gift Genie](https://gift-genie.onrender.com/)

---

### About The Project

Gift Genie was born from a common problem: finding a truly thoughtful gift. Generic "Top 10" lists and e-commerce recommendations just don't cut it.

This app solves that by acting as a "wrapper" around a powerful Large Language Model (LLM). Instead of just giving the user a blank text box, Gift Genie guides them through a friendly, conversational "interview" to build a rich profile of the gift recipient. It asks about their personality, interests, relationship, and recent life events, using this deep context to generate personalized and creative gift recommendations.

### 🚀 Features

* **Conversational UI:** A clean, real-time chatbot interface.
* **Dynamic Questions:** The AI asks relevant, option-based questions based on your previous answers, ensuring a natural conversation.
* **Psychological Profiling:** Goes beyond surface-level interests (age, gender) to understand personality, needs, and relationship dynamics.
* **LLM-Powered:** Uses a local, open-source LLM (`llama3:8b`) via Ollama for all question and recommendation generation.
* **Stateful Conversations:** Uses `HttpSession` to remember your chat history during a single session.
* **Containerized:** Fully containerized with Docker for easy, reproducible, all-in-one deployment.

---

### 💻 Tech Stack

* **Backend:** Spring Boot, Spring AI
* **LLM Service:** Ollama
* **LLM Model:** Llama 3 (`llama3:8b`)
* **Frontend:** HTML, CSS, JavaScript (served by Spring Boot using Thymeleaf)
* **Build:** Maven
* **Containerization:** Docker

---

### 🏁 Getting Started (Local Development)

You can run the full application on your local machine.

#### Prerequisites

1.  **JDK 21** (or 17)
2.  **Maven**
3.  **Ollama:** You **must** have the [Ollama](https://ollama.com/) desktop application installed and running.
4.  **Pull the LLM Model:** Run this command in your terminal to download the model:
    ```sh
    ollama pull llama3:8b
    ```

#### Running the App

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/your-username/Gift-Genie.git](https://github.com/your-username/Gift-Genie.git)
    cd Gift-Genie
    ```

2.  **Run the Spring Boot application:**
    ```sh
    mvn spring-boot:run
    ```

3.  **Open your browser:**
    Navigate to `http://localhost:10000` (or `http://localhost:8080` if you changed the default port in `application.properties`).

**Note:** The local app will connect to your local Ollama application, as defined in `application.properties` (`spring.ai.ollama.base-url=http://localhost:11434`).

---

### 📦 Deployment (Production)

This project is configured for a single-container "all-in-one" deployment on platforms like [Render](https://render.com/).

The `Dockerfile` is a multi-stage build that:
1.  Builds the Spring Boot `.jar` file using a Maven image.
2.  Creates a final production image from a Java runtime.
3.  Installs the Ollama service directly into the container.
4.  Copies the Spring Boot `.jar` and a custom `start.sh` script.

The `start.sh` script is the key: it runs **both** the Ollama server (in the background) and the Spring Boot application (in the foreground), allowing us to deploy two services in one free container.

The `application.properties` file is set to use `server.port=${PORT:10000}` to automatically pick up the port assigned by the deployment platform.

---


