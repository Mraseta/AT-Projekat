import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  address = "";
  host = "";
  socket: any;

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.address = this.router.url.split(":")[0];
    console.log(this.address);
    this.host = "ws://" + this.address + ":8080/ChatWAR/ws";

    try {
      this.socket = new WebSocket(this.host);
      var self = this;
      //console.log('connect: Socket Status: '+ this.socket.readyState);

      this.socket.onopen = function () {
        //console.log('onopen: Socket Status: '+ this.socket.readyState+' (open)');
        //console.log('onopen: LogedUser: '+user+'');
      }

      this.socket.onmessage = function (msg) {
        console.log('onmessage: Received: ' + msg.data);
      }

      this.socket.onclose = function () {
        this.socket = null;
      }

    } catch (exception) {
      console.log('Error' + exception);
    }
  }

}
