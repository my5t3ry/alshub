import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {NotifierService} from "angular-notifier";
import {RequestInterceptorService} from "../../request-interceptor.service";

@Component({
  selector: 'app-project-git-graph',
  templateUrl: './project-git-graph.component.html',
  styleUrls: ['./project-git-graph.component.scss']
})
export class ProjectGitGraphComponent implements OnInit {

  @Input() projectId: number;
  endpoint = '/api/project/get-commit-history';
  private commitHistory: any;


  constructor(private http: HttpClient, private route: ActivatedRoute, private notifierService: NotifierService) {
  }


  ngOnInit() {
    this.http.get<any>(this.endpoint + "/" + this.projectId, RequestInterceptorService.httpOptions).pipe().subscribe(value => {
      this.commitHistory = value;
      console.log(value)
    })
  }

}