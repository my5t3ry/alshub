import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";

@Component({
  selector: 'app-my-projects',
  templateUrl: './my-projects.component.html',
  styleUrls: ['./my-projects.component.scss']
})
export class MyProjectsComponent implements OnInit {
  private endpoint = 'http://localhost:8090/api/project';
  private httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'Accept': 'application/json'})
  };
  private projects: any;

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.fetchItems();
  }

  fetchItems() {
    this.getClients().subscribe(data => {
      this.projects = data
    });
  }

  getClients(): Observable<any> {
    return this.http.get<any>(this.endpoint + "/my-projects", this.httpOptions).pipe();
  }

}
