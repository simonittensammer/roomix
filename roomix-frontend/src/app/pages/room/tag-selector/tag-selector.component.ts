import {Component, ElementRef, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {map, startWith} from 'rxjs/operators';
import {MatChipInputEvent} from '@angular/material/chips';
import {MatAutocomplete, MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import {RoomlistService} from '../../../services/roomlist.service';
import {Tag} from '../../../models/tag';
import { EventEmitter } from '@angular/core';

@Component({
  selector: 'app-tag-selector',
  templateUrl: './tag-selector.component.html',
  styleUrls: ['./tag-selector.component.scss'],
})
export class TagSelectorComponent implements OnInit{
  visible = true;
  selectable = true;
  removable = true;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  tagCtrl = new FormControl();
  filteredTags: Observable<string[]>;
  tags: string[] = [];
  allTags: string[] = [];

  @Output() childToParent = new EventEmitter<Tag[]>();
  @Input('input-tags') inputTags: Tag[];

  @ViewChild('tagInput') tagInput: ElementRef<HTMLInputElement>;
  @ViewChild('auto') matAutocomplete: MatAutocomplete;

  constructor(
      private roomListService: RoomlistService
  ) {
    this.filteredTags = this.tagCtrl.valueChanges.pipe(
        startWith(null),
        map((tag: string | null) => tag ? this._filter(tag) : this.allTags.slice()));
  }

  ngOnInit(): void {
    this.roomListService.getAllTags().subscribe(tags => {
      this.allTags = tags.map(tag => tag.name);
      this.inputTags.forEach(tag => {
        this.tags.push(tag.name);
        this.allTags.splice(this.allTags.indexOf(tag.name), 1);
      });
    });
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if ((value || '').trim()) {
      this.tags.push(value.trim());
    }

    if (input) {
      input.value = '';
    }

    this.tagCtrl.setValue(null);
  }

  remove(tag: string): void {
    const index = this.tags.indexOf(tag);

    if (index >= 0) {
      this.tags.splice(index, 1);
      this.allTags.push(tag);
      this.sendToParent();
    }
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    const tag = event.option.viewValue;
    this.select(tag);
  }

  select(tag: string) {
    this.tags.push(tag);
    if (this.allTags.includes(tag)) {
      this.allTags.splice(this.allTags.indexOf(tag), 1);
    }
    this.tagInput.nativeElement.value = '';
    this.tagCtrl.setValue(null);
    this.sendToParent();
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.allTags.filter(tag => tag.toLowerCase().indexOf(filterValue) === 0);
  }

  sendToParent(): void {
    this.childToParent.emit(this.tags.map(name => new Tag(name)));
  }
}
