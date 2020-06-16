import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgentService {

  constructor(private http: HttpClient) { }

  getAgents(address: string) {
    return this.http.get("http://" + address + ":8080/ATProjWAR/rest/agents/running")
      .pipe(
        map((res: any) => {
          const data = res;
          return data;
        }),
        catchError((err: any) => {
          console.log(err)
          return throwError(err)
        })
      )
  }

  getAgentTypes(address: string) {
    return this.http.get("http://" + address + ":8080/ATProjWAR/rest/agents/classes")
      .pipe(
        map((res: any) => {
          const data = res;
          return data;
        }),
        catchError((err: any) => {
          console.log(err)
          return throwError(err)
        })
      )
  }

  getMessages(address: string) {
    return this.http.get("http://" + address + ":8080/ATProjWAR/rest/messages")
      .pipe(
        map((res: any) => {
          const data = res;
          return data;
        }),
        catchError((err: any) => {
          console.log(err)
          return throwError(err)
        })
      )
  }

  sendMessage(team1: string, team2: string, agent: any, address: string) {
    let receivers = [];
    receivers.push(agent.id);
    console.log(receivers);
    return this.http.post("http://" + address + ":8080/ATProjWAR/rest/messages", {
      "performative": 23,
      "content": team1+'-'+team2,
      "receivers": receivers
    })
      .pipe(
        map((res: any) => {
          const data = res;
          localStorage.setItem('loggedUser', JSON.stringify(data));
          return data;
        }),
        catchError((err: any) => {
          console.log(err)
          return throwError(err)
        })
      )
  }
}
