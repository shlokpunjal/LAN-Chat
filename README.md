# LAN Chat — Real-Time Chat Application

A multi-client real-time chat application built in Java, running entirely over a local network. No internet required. Messages persist across sessions via a MySQL database.

---

## The Stack

- **Java** — core application logic
- **Java Swing** — desktop UI with custom-painted gradient windows
- **Socket Programming** — TCP client-server communication over LAN
- **MySQL** — message persistence with JDBC connector
- **Multithreading** — each client handled on a dedicated daemon thread

---

## Architecture

```
LoginWindow → Client → Server → ClientHandler (per client)
                                      ↓
                                  DBHelper (MySQL)
                                      ↓
                              ChatWindow (live UI)
```

- `Server.java` — starts on port 9090, accepts clients, broadcasts messages
- `Client.java` — connects to server, listens for incoming messages on a background thread
- `ClientHandler.java` — handles one client per thread, saves messages to DB
- `DBHelper.java` — MySQL connection, table creation, save and load messages
- `LoginWindow.java` — entry UI, takes username and server IP
- `ChatWindow.java` — live chat UI with color-coded messages and history loading

---

## Features

- Multiple clients chat simultaneously over LAN
- Messages broadcast to all connected clients in real time
- Chat history loaded from MySQL on every session start
- Color-coded messages: your messages, others, and server announcements
- Clean login screen with server IP input for flexible network use
- Join and leave notifications for all connected users

---

## Setup

**Prerequisites**
- Java 17+
- MySQL running locally
- `mysql-connector-j-9.6.0.jar`

**Database**

Create the database before running:
```sql
CREATE DATABASE chatapp;
```
The `messages` table is created automatically on first run.

**Configuration**

Create a `config.properties` file in the project root:
```
DB_URL=jdbc:mysql://localhost:3306/chatapp
DB_USER=your_username
DB_PASSWORD=your_password
```

**Run the Server**
```
javac -cp lib/mysql-connector-j-9.6.0.jar *.java
java -cp .:lib/mysql-connector-j-9.6.0.jar Server
```

**Run the Client** *(on any machine on the same network)*
```
java -cp .:lib/mysql-connector-j-9.6.0.jar LoginWindow
```
Enter a username and the server machine's local IP address to connect.

---

## Demo

https://github.com/shlokpunjal/LAN-Chat/blob/main/Output.mp4

## Learnings

Socket programming forces you to think about things that higher-level frameworks hide: connection lifecycle, thread management, what happens when a client drops unexpectedly. Building this from scratch made those concepts concrete in a way no tutorial could.

Swing isn't pretty by default. Getting the gradient backgrounds, hover effects, and color-coded message rendering to work required digging into `paintComponent` and `StyledDocument` — parts of the API most people never touch.

---

*Built with Java · Socket Programming · MySQL · Swing*
