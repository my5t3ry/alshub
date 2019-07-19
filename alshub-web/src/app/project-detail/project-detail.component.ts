import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  projectId: any;
  private endpoint = 'http://localhost:8090/api/project';
  private httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'Accept': 'application/json'})
  };
  project: any;
  changes: any;

  constructor(private http: HttpClient, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.projectId = params['projectId'];
      this.receiveProject(this.projectId).subscribe(data => {
        this.project = data;
      });
      this.fetchChanges();
    });
  }

  private fetchChanges() {
    this.receiveChanges(this.projectId).subscribe(data => {
      this.changes = data.changeList;
    });
  }

  receiveProject(id): Observable<any> {
    return this.http.get<any>(this.endpoint + "/" + id, this.httpOptions).pipe();
  }

  receiveChanges(id): Observable<any> {
    return this.http.get<any>(this.endpoint + "/get-changes/" + id, this.httpOptions).pipe();
  }

  checkForChanges() {
    this.fetchChanges();
  }
}
