import {Component, Input, OnInit} from '@angular/core';
import {RequestInterceptorService} from "../../request-interceptor.service";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-project-card-meta-data',
  templateUrl: './project-card-meta-data.component.html',
  styleUrls: ['./project-card-meta-data.component.scss']
})
export class ProjectCardMetaDataComponent implements OnInit {
  private endpoint = 'http://localhost:8090/api/project-meta-data';
  @Input() project: any;
  projectMetaData: any;

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.http.get<any>(this.endpoint + "/" + this.project.id, RequestInterceptorService.httpOptions).pipe().subscribe(value => {
      this.projectMetaData = value;
    })
  }
}
