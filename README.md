# Manech Manager 📱

Manech Manager is an Android application designed for the efficient management, configuration, and monitoring of Manech Node entities. It provides a centralized interface to track device states, manage network settings, and quickly deploy configurations using QR codes.

## ✨ Features

### 🛠 Node Management
- **Centralized Node List**: View all registered nodes with their current status, IP addresses, and custom descriptions.
- **Rapid Deployment**: Duplicate existing node configurations to quickly add similar devices.
- **Detailed Configuration**: Manage Access Point (AP) settings, WiFi credentials, and device-specific identifiers.
- **Visual Identification**: Assign colors to nodes for easier identification in large-scale deployments.

### ⚡ QR Code Integration
- **Instant Import**: Use the built-in high-performance scanner (powered by Google ML Kit) to import node configurations via QR codes.
- **Configuration Export**: Generate QR codes for any node's configuration to share settings across devices.
- **Bulk Printing**: Export all node QR codes into a professional PDF format for physical labeling and installation.

### 🌐 Network & Monitoring
- **Real-time State Tracking**: Monitor whether nodes are Online or Offline.
- **Remote Configuration**: Push configuration updates directly to devices via HTTP API.
- **Connectivity Management**: Manage WiFi and Gateway settings for each node.

## 🛠 Technical Stack

- **Language**: Java
- **Platform**: Android (SDK 33)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Database**: Room Persistence Library
- **Networking**: Volley
- **Scanning**: Google ML Kit & CameraX
- **QR Generation**: ZXing Core

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 33
- A physical Android device (required for Camera and QR scanning features)

### Installation
1. Clone the repository:

