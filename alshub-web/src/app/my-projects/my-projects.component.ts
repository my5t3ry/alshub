import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {RequestInterceptorService} from "../../request-interceptor.service";

@Component({
  selector: 'app-my-projects',
  templateUrl: './my-projects.component.html',
  styleUrls: ['./my-projects.component.scss']
})
export class MyProjectsComponent implements OnInit {
  endpoint = '/api/project';

  projects: any;

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
    return this.http.get<any>(this.endpoint + "/my-projects", RequestInterceptorService.httpOptions).pipe();
  }

}
