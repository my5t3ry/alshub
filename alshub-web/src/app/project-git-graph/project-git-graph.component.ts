import {Component, ElementRef, Input, OnInit, Renderer2, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {NotifierService} from "angular-notifier";
import {RequestInterceptorService} from "../request-interceptor.service";
import {Observable} from "rxjs";
import {GitPlotService} from "../git-plot.service";
import {ContextMenuComponent, ContextMenuService, ExecuteContextMenuEvent} from "ngx-contextmenu";
import * as SRD from "storm-react-diagrams"
import * as React from "react";
import ReactDOM from "react-dom";
import {DiagramEngine} from "storm-react-diagrams";
import {DiagramModel} from "storm-react-diagrams";
import {DefaultNodeModel} from "storm-react-diagrams";
import { StormWidget} from "./storm";


@Component({
  selector: 'app-project-git-graph',
  templateUrl: './project-git-graph.component.html',
  styleUrls: ['./project-git-graph.component.scss']
})
export class ProjectGitGraphComponent implements OnInit {


  @Input() projectId: number;
  @ViewChild('divElement', {static: false}) public divElement: ElementRef;
  @ViewChild(ContextMenuComponent, {static: false}) public basicMenu: ContextMenuComponent;


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


  constructor(private contextMenuService: ContextMenuService, private http: HttpClient, private route: ActivatedRoute, private notifierService: NotifierService, private gitPlotService: GitPlotService, private renderer: Renderer2) {
    this.gitPlotService.renderer = renderer;
  }


  ngOnInit() {
    this.http.get<any>(this.endpoint + "/" + '/get-commit-history/' + this.projectId, RequestInterceptorService.httpOptions).pipe().subscribe(value => this.drawPlot(value));
  }
  //
  // createStormEngine() {
  //   //1) setup the diagram engine
  //   let engine = new DiagramEngine();
  //   engine.installDefaultFactories();
  //
  //   //2) setup the diagram model
  //   let model = new DiagramModel();
  //
  //   //3) create a default nodes
  //   let nodesFrom = [];
  //   let nodesTo = [];
  //
  //   nodesFrom.push(createNode("from-1"));
  //   nodesFrom.push(createNode("from-2"));
  //   nodesFrom.push(createNode("from-3"));
  //
  //   nodesTo.push(createNode("to-1"));
  //   nodesTo.push(createNode("to-2"));
  //   nodesTo.push(createNode("to-3"));
  //
  //   //4) link nodes together
  //   let links = nodesFrom.map((node, index) => {
  //     return connectNodes(node, nodesTo[index]);
  //   });
  //
  //   // more links for more complicated diagram
  //   links.push(connectNodes(nodesFrom[0], nodesTo[1]));
  //   links.push(connectNodes(nodesTo[0], nodesFrom[1]));
  //   links.push(connectNodes(nodesFrom[1], nodesTo[2]));
  //
  //   // initial random position
  //   nodesFrom.forEach((node, index) => {
  //     node.x = index * 70;
  //     model.addNode(node);
  //   });
  //
  //   nodesTo.forEach((node, index) => {
  //     node.x = index * 70;
  //     node.y = 100;
  //     model.addNode(node);
  //   });
  //
  //   links.forEach(link => {
  //     model.addLink(link);
  //   });
  //
  //   //5) load model into engine
  //   let model2 = getDistributedModel(engine, model);
  //
  //   engine.setDiagramModel(model2);
  //   let distributedModel = getDistributedModel(engine, model2);
  //   engine.setDiagramModel(distributedModel);
  //   return engine;
  // }

  private drawPlot(value) {
    this._data = value;
    if (!this.graphInit) {
      let detailedReactHTMLElement = React.createElement(StormWidget);
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

  private mouseOver(commit: any, event: MouseEvent) {
    this.onContextMenu(event, commit);
  }

  private mouseOut(commit: any, event: MouseEvent) {
    // this.onContextMenu(event, commit);
    // this.contextMenuService.closeAllContextMenus({
    //   eventType: 'cancel',
    //   event: event
    // });
  }

  public onContextMenu($event: MouseEvent, item: any): void {
    this.contextMenuService.show.next({
      // Optional - if unspecified, all context menu components will open
      contextMenu: this.basicMenu,
      event: $event,
      item: item,
    });
    $event.preventDefault();
    $event.stopPropagation();
  }
}
