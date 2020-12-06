package com.example.cotton.ValueObject.BookSearchByBarcode;

import org.simpleframework.xml.Element;

public class ItemVO {
        @Element(name = "title")
        private String title;

        @Element(name = "link")
        private String link;

        @Element(name = "image")
        private String image;

        @Element(name = "author")
        private String author;

        @Element(name = "price")
        private String price;

        @Element(name = "discount")
        private String disocunt;

        @Element(name = "publisher")
        private String publisher;

        @Element(name = "pubdate")
        private String pubdate;

        @Element(name = "isbn")
        private String isbn;

        @Element(name = "description")
        private String description;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDisocunt() {
            return disocunt;
        }

        public void setDisocunt(String disocunt) {
            this.disocunt = disocunt;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getPubdate() {
            return pubdate;
        }

        public void setPubdate(String pubdate) {
            this.pubdate = pubdate;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
}
