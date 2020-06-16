import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AgentService } from '../services/agent.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  address = "";
  host = "";
  socket: any;
  agents = [];
  agentsLoaded = false;
  agentTypes = [];
  typesLoaded = false;
  messages = [];
  messagesLoaded = false;
  teams = ["FC BARCELONA", "REAL MADRID", "SEVILLA FC", "REAL SOCIEDAD", "GETAFE CF", "ATLETICO DE MADRID", "VALENCIA CF", "GRANADA CF", "VILLAREAL CF", "ATHLETIC CLUB", "C.A. OSASUNA", "LEVANTE UD", "REAL BETIS", "REAL VALLADOLID CF", "D. ALAVES", "SD EIBAR", "RC CELTA", "RCD MALLORCA", "CD LEGANES", "RCD ESPANYOL"];
  selected1 = null;
  selected2 = null;

  constructor(private router: Router,
    private agentService: AgentService) { }

  ngOnInit(): void {
    this.address = window.location.href.split(":")[1];
    this.address = this.address.substring(2);
    console.log(this.address);
    this.address = "192.168.0.12"; // ovo zakomentarisati posle
    this.host = "ws://" + this.address + ":8080/ATProjWAR/ws";

    this.getAgents();

    try {
      this.socket = new WebSocket(this.host);
      var self = this;
      //console.log('connect: Socket Status: '+ this.socket.readyState);

      this.socket.onopen = function () {
        console.log('onopen');
        //console.log('onopen: Socket Status: '+ this.socket.readyState+' (open)');
        //console.log('onopen: LogedUser: '+user+'');
      }

      this.socket.onmessage = function (msg) {
        console.log('onmessage: Received: ' + msg.data);
        if(msg.data.includes('predicted') || msg.data.includes('Draw')) {
          alert(msg.data);
        }

        if(msg.data.includes('messages')) {
          self.getMessages();
        }
        
        if(msg.data.includes('agents')) {
          self.getAgents();
        }
        
        if(msg.data.includes('types')) {
          self.getAgentTypes();
        }
      }

      this.socket.onclose = function () {
        this.socket = null;
      }

    } catch (exception) {
      console.log('Error' + exception);
    }
  }

  getAgents() {
    this.agentService.getAgents(this.address)
      .subscribe(
        (data: any) => {
          console.log('Got agents.');
          console.log(data);
          this.agents = Object.assign([], (data));
          this.agentsLoaded = true;
        }, (error) => alert(error.text)
      );
  }

  getAgentTypes() {
    this.agentService.getAgentTypes(this.address)
      .subscribe(
        (data: any) => {
          console.log('Got agent types.');
          console.log(data);
          this.agentTypes = Object.assign([], (data));
          this.typesLoaded = true;
        }, (error) => alert(error.text)
      );
  }

  getMessages() {
    this.agentService.getMessages(this.address)
      .subscribe(
        (data: any) => {
          console.log('Got agent types.');
          console.log(data);
          this.messages = Object.assign([], (data));
          this.messagesLoaded = true;
        }, (error) => alert(error.text)
      );
  }

  aaa() {
    let agent = null;
    for(let a of this.agents) {
      if(a.id.type.name === 'collector') {
        agent = a;
        break;
      }
    }
    console.log(agent);
    this.agentService.sendMessage(this.selected1, this.selected2, agent, this.address)
      .subscribe(
        (data: any) => {
          console.log(data);
        }, (error) => alert(error.text)
      );
  }

}
