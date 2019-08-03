import {Component, ElementRef, Input, OnInit, Renderer2, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {NotifierService} from "angular-notifier";
import {RequestInterceptorService} from "../request-interceptor.service";
import {Observable} from "rxjs";
import {GitPlotService} from "../git-plot.service";
import ReactDOM from 'react-dom';
import * as React from "react";
import {TreeComponent} from "./tree";

@Component({
  selector: 'app-project-git-graph',
  templateUrl: './project-git-graph.component.html',
  styleUrls: ['./project-git-graph.component.scss']
})
export class ProjectGitGraphComponent implements OnInit {


  @Input() projectId: number;
  @ViewChild('divElement', {static: false}) public divElement: ElementRef;


  endpoint = '/api/project/';
  changes: any;
  private commitHistory: any;
  private eveentMethods = {
    onClick: this.restoreCommit.bind(this)
    ,
    onMessageClick: this.restoreCommit.bind(this)
    ,
    onMouseOver(commit) {
      // …
    }
    ,
    onMouseOut(commit) {
      // …
    }
  }
  private _data: any;
  private graphInit: boolean = false;


  constructor(private http: HttpClient, private route: ActivatedRoute, private notifierService: NotifierService, private gitPlotService: GitPlotService, private renderer: Renderer2) {
    this.gitPlotService.renderer = renderer;
  }


  ngOnInit() {
    this.http.get<any>(this.endpoint + "/" + '/get-commit-history/' + this.projectId, RequestInterceptorService.httpOptions).pipe().subscribe(value => this.drawPlot(value));
  }

  private drawPlot(value) {
    this._data = value;
    if (!this.graphInit) {
      let detailedReactHTMLElement = React.createElement(TreeComponent, {data: {name: "test", children: this._data}, onClickHandler: this.restoreCommit.bind(this)}, null);
      ReactDOM.render(detailedReactHTMLElement, this.divElement.nativeElement);
    }
  }

  restoreCommit(commit: any) {
    this.http.get<any>(this.endpoint + '/restore-commit/' + this.projectId + "/" + commit.hash, RequestInterceptorService.httpOptions).pipe().subscribe(value => this.drawPlot(value))
  }

  receiveChanges(id): Observable<any> {
    return this.http.get<any>(this.endpoint + "/get-changes/" + this.projectId, RequestInterceptorService.httpOptions).pipe();
  }

  public checkForChanges() {
    this.fetchChanges();
  }

  public pushChanges(project: any) {
    return this.http.get<any>(this.endpoint + "/push-changes/" + project.id, RequestInterceptorService.httpOptions).pipe().subscribe(value => this.drawPlot(value));
  }

  public fetchChanges() {
    this.receiveChanges(this.projectId).subscribe(data => {
      this.changes = data.changeList;
    });
  }

}
