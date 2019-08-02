import {ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';

class Picture {
  id: number;
  base64: string;
  motive: number;
  hash: string;
}

@Component({
  selector: 'app-picture-gallery',
  templateUrl: './picture-gallery.component.html',
  styleUrls: ['./picture-gallery.component.scss']
})
export class PictureGalleryComponent implements OnInit {
  private galleryModal: NgbModalRef;

  @Output() onPictureSelect: EventEmitter<string> = new EventEmitter();
  @ViewChild('deletePictureConfirmationContent') deleteDialogModal: ElementRef;
  @Input() editMode = false;
  private _pictures: Picture[];
  private endpoint = 'http://localhost/api/picture';
  private file: string | ArrayBuffer;
  private httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'Accept': 'application/json'})
  };

  constructor(private modalService: NgbModal, private http: HttpClient, private cd: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.refreshPictures();
  }

  refreshPictures() {
    this.loadPictures().subscribe(result => {
      this._pictures = result;
    });
  }

  onFileChange(event) {
    const reader = new FileReader();
    if (event.target.files && event.target.files.length) {
      const [file] = event.target.files;
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.file = reader.result;
        this.cd.markForCheck();
        this.uploadPicture();
      };
    }
  }

  loadPictures(): Observable<Picture[]> {
    return this.http.get<Picture[]>(this.endpoint + '/').pipe(source => {
      return source;
    });
  }

  deletePicture(pictureId): Observable<Picture[]> {
    return this.http.get<Picture[]>(this.endpoint + '/delete/' + pictureId).pipe(source => {
      return source;
    });
  }

  get pictures(): Picture[] {
    return this._pictures;
  }

  showPictureSelectModal(content) {
    this.galleryModal = this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', windowClass: 'picture-gallery'});
  }

  clickPicture(event: Event) {
    if (this.editMode) {
      this.modalService.open(this.deleteDialogModal, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
        this.deletePicture(event.srcElement.attributes.getNamedItem('data-id').value).subscribe(result => {
          this.loadPictures().subscribe(result => {
            this._pictures = result;
          });
        });
      }, (reason) => {
      });
    } else {
      this.onPictureSelect.emit(event.srcElement.attributes.getNamedItem('src').value);
      this.galleryModal.close();
    }
  }

  private uploadPicture() {
    const pictureSaveRequest = { 'data': this.file};
    this.saveCampaign(pictureSaveRequest).subscribe(value => {
      this.refreshPictures();
    });
  }

  saveCampaign(pictureSaveRequest): Observable<Picture> {
    return this.http.post<Picture>(this.endpoint, JSON.stringify(pictureSaveRequest), this.httpOptions).pipe();
  }
}
