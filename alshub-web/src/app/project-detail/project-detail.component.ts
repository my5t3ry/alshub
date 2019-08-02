import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {NotifierService} from "angular-notifier";
import {RequestInterceptorService} from "../../request-interceptor.service";

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  projectId: any;
  endpoint = '/api/project';

  project: any;
  changes: any;

  constructor(private http: HttpClient, private route: ActivatedRoute, private notifierService: NotifierService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.projectId = params['projectId'];
      this.receiveProject(this.projectId).subscribe(data => {
        this.project = data;
      });
      // this.fetchChanges();
    });
  }

  fetchChanges() {
    this.receiveChanges(this.projectId).subscribe(data => {
      this.changes = data.changeList;
    });
  }

  receiveProject(id): Observable<any> {
    return this.http.get<any>(this.endpoint + "/" + id, RequestInterceptorService.httpOptions).pipe();
  }

  receiveChanges(id): Observable<any> {
    return this.http.get<any>(this.endpoint + "/get-changes/" + id, RequestInterceptorService.httpOptions).pipe();
  }

  checkForChanges() {
    this.fetchChanges();
  }

  pushChanges(project: any) {
    return this.http.get<any>(this.endpoint + "/push-changes/" + project.id, RequestInterceptorService.httpOptions).pipe().subscribe(value => this.checkForChanges());

  }
}
