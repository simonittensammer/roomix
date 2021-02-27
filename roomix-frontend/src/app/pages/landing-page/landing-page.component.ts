import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss'],
})
export class LandingPageComponent implements OnInit {
  @ViewChild('scrollBox') private scrollBox: ElementRef;
  scrollPx: number;

  constructor() { }

  ngOnInit() {}

  onScroll($event: Event) {
    this.scrollPx = this.scrollBox.nativeElement.scrollTop;
  }
}
