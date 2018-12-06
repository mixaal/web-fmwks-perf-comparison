# Rust Restful example

## Get Rust

```
curl https://sh.rustup.rs -sSf | sh
```

## Compile and run

```
cargo run --release
```

## Test performance

```
ab -n 1000 -c 4 http://localhost:3000/
```

