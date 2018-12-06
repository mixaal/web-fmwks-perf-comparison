extern crate hyper;

use hyper::{Body, Request, Response, Server};
use hyper::rt::Future;
use hyper::service::service_fn_ok;

const HELLO: &str = "Hello, world!";

fn get_people(_req: Request<Body>) -> Response<Body> {
    Response::new(Body::from(HELLO))
}

fn main() {
    let addr = ([127, 0, 0, 1], 3000).into();

    let new_svc = || {
        service_fn_ok(get_people)
    };

    let server = Server::bind(&addr)
        .serve(new_svc)
        .map_err(|e| eprintln!("server error: {}", e));

    hyper::rt::run(server)
}
