import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';

export interface WebSocketConfig {
  endpoint: string;
  reconnectDelay?: number;
  heartbeatIncoming?: number;
  heartbeatOutgoing?: number;
  debug?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClients: Map<string, Client> = new Map();
  private subscriptions: Map<string, { client: Client; subscription: StompSubscription }> = new Map();
  private connectionStatus$ = new BehaviorSubject<boolean>(false);
  private configs: Map<string, WebSocketConfig> = new Map();

  constructor() {}

  /**
   * Connect to WebSocket server
   */
  connect(config: WebSocketConfig): Promise<void> {
    return new Promise((resolve, reject) => {
      const clientKey = config.endpoint;
      
      // Check if already connected to this endpoint
      const existingClient = this.stompClients.get(clientKey);
      if (existingClient?.active) {
        console.log(`WebSocket already connected to ${clientKey}`);
        resolve();
        return;
      }

      this.configs.set(clientKey, config);

      const client = new Client({
        webSocketFactory: () => new SockJS(config.endpoint),
        reconnectDelay: config.reconnectDelay || 5000,
        heartbeatIncoming: config.heartbeatIncoming || 4000,
        heartbeatOutgoing: config.heartbeatOutgoing || 4000,
        debug: config.debug ? (str) => console.log(`[WS ${clientKey}]`, str) : undefined,
      });

      client.onConnect = (frame) => {
        console.log(`✅ WebSocket connected to ${clientKey}:`, frame);
        this.connectionStatus$.next(true);
        resolve();
      };

      client.onStompError = (frame) => {
        console.error(`❌ WebSocket error on ${clientKey}:`, frame.headers['message']);
        console.error('Details:', frame.body);
        this.connectionStatus$.next(false);
        reject(new Error(frame.headers['message']));
      };

      client.onWebSocketClose = () => {
        console.log(`🔌 WebSocket connection closed for ${clientKey}`);
        this.stompClients.delete(clientKey);
        // Update status only if no other clients are active
        if (this.stompClients.size === 0) {
          this.connectionStatus$.next(false);
        }
      };

      this.stompClients.set(clientKey, client);
      client.activate();
    });
  }

  /**
   * Disconnect from WebSocket server
   */
  disconnect(endpoint?: string): Promise<void> {
    return new Promise((resolve) => {
      if (endpoint) {
        // Disconnect specific endpoint
        const client = this.stompClients.get(endpoint);
        if (!client) {
          resolve();
          return;
        }

        // Unsubscribe from all topics for this client
        const subsToRemove: string[] = [];
        this.subscriptions.forEach((value, key) => {
          if (value.client === client) {
            value.subscription.unsubscribe();
            subsToRemove.push(key);
          }
        });
        subsToRemove.forEach(key => this.subscriptions.delete(key));

        client.onDisconnect = () => {
          console.log(`🔌 WebSocket disconnected from ${endpoint}`);
          this.stompClients.delete(endpoint);
          if (this.stompClients.size === 0) {
            this.connectionStatus$.next(false);
          }
          resolve();
        };

        client.deactivate();
      } else {
        // Disconnect all
        if (this.stompClients.size === 0) {
          resolve();
          return;
        }

        // Unsubscribe from all topics
        this.subscriptions.forEach((value) => value.subscription.unsubscribe());
        this.subscriptions.clear();

        const disconnectPromises = Array.from(this.stompClients.values()).map(client => {
          return new Promise<void>((res) => {
            client.onDisconnect = () => res();
            client.deactivate();
          });
        });

        Promise.all(disconnectPromises).then(() => {
          console.log('🔌 All WebSocket connections disconnected');
          this.stompClients.clear();
          this.connectionStatus$.next(false);
          resolve();
        });
      }
    });
  }

  /**
   * Subscribe to a topic
   */
  subscribe<T>(destination: string, callback: (message: T) => void, endpoint?: string): string {
    // Find the appropriate client
    let client: Client | null = null;
    
    if (endpoint) {
      client = this.stompClients.get(endpoint) || null;
    } else {
      // Use the first active client
      for (const c of this.stompClients.values()) {
        if (c.active) {
          client = c;
          break;
        }
      }
    }

    if (!client?.active) {
      throw new Error('WebSocket not connected. Call connect() first.');
    }

    const subscriptionId = `sub-${Date.now()}-${Math.random()}`;

    const subscription = client.subscribe(destination, (message: IMessage) => {
      try {
        const parsedMessage = JSON.parse(message.body);
        callback(parsedMessage);
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
        callback(message.body as any);
      }
    });

    this.subscriptions.set(subscriptionId, { client, subscription });
    console.log(`📡 Subscribed to ${destination} with ID: ${subscriptionId}`);

    return subscriptionId;
  }

  /**
   * Unsubscribe from a topic
   */
  unsubscribe(subscriptionId: string): void {
    const sub = this.subscriptions.get(subscriptionId);
    if (sub) {
      sub.subscription.unsubscribe();
      this.subscriptions.delete(subscriptionId);
      console.log(`🔕 Unsubscribed from subscription ID: ${subscriptionId}`);
    }
  }

  /**
   * Send a message to a destination
   */
  send(destination: string, body: any, endpoint?: string): void {
    let client: Client | null = null;
    
    if (endpoint) {
      client = this.stompClients.get(endpoint) || null;
    } else {
      // Use the first active client
      for (const c of this.stompClients.values()) {
        if (c.active) {
          client = c;
          break;
        }
      }
    }

    if (!client?.active) {
      throw new Error('WebSocket not connected. Call connect() first.');
    }

    client.publish({
      destination,
      body: JSON.stringify(body),
    });

    console.log(`📤 Message sent to ${destination}:`, body);
  }

  /**
   * Get connection status as Observable
   */
  getConnectionStatus(): Observable<boolean> {
    return this.connectionStatus$.asObservable();
  }

  /**
   * Check if connected
   */
  isConnected(endpoint?: string): boolean {
    if (endpoint) {
      return this.stompClients.get(endpoint)?.active || false;
    }
    // Return true if any client is connected
    for (const client of this.stompClients.values()) {
      if (client.active) return true;
    }
    return false;
  }

  /**
   * Reconnect to WebSocket server
   */
  async reconnect(endpoint?: string): Promise<void> {
    if (endpoint) {
      const config = this.configs.get(endpoint);
      if (config) {
        await this.disconnect(endpoint);
        await this.connect(config);
      }
    } else {
      // Reconnect all
      const allConfigs = Array.from(this.configs.entries());
      await this.disconnect();
      for (const [_, config] of allConfigs) {
        await this.connect(config);
      }
    }
  }
}
