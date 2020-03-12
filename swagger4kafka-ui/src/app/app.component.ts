import {Component, OnInit} from '@angular/core';
import {AsyncApiDocService} from './services/doc.service';
import {AsyncApiDoc} from './shared/async-api/doc.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [AsyncApiDocService]
})
export class AppComponent implements OnInit {
  doc: AsyncApiDoc;

  constructor(private asyncApiDocService: AsyncApiDocService) {}

  ngOnInit(): void {
    this.asyncApiDocService
      .getAsyncApiDoc()
      .subscribe(doc => this.doc = doc);
  }

}
