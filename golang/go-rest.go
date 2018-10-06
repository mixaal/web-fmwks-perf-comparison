package main

import (
	"net/http"
	"github.com/gorilla/mux"
	"log"
)

func GetPeople(w http.ResponseWriter, r *http.Request) {
	w.Write([]byte("Hello world!"))
}

func main() {
	router := mux.NewRouter()
	router.HandleFunc("/", GetPeople).Methods("GET")
	log.Fatal(http.ListenAndServe(":8000", router))
}
