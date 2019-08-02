import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {NotifierService} from "angular-notifier";
import {RequestInterceptorService} from "../request-interceptor.service";
import {ProjectGitGraphComponent} from "../project-git-graph/project-git-graph.component";

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  projectId: any;
  endpoint = '/api/project/';

  project: any;
  @ViewChild(ProjectGitGraphComponent,{static: false}) public projectGitGraph:ProjectGitGraphComponent;
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

  receiveProject(id): Observable<any> {
    return this.http.get<any>(this.endpoint + "/" + id, RequestInterceptorService.httpOptions).pipe();
  }

}
