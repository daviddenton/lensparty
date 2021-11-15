# Lens Party

Firstly, look at `types.kt`. In it, we have the definition of Extract, Inject and an exception:

- `Extract<ENTITY, PART>` - A function which returns a PART which has been extracted from ENTITY.
- `Inject<PART, ENTITY>` - A function which returns a copy of ENTITY with PART injected into it.

### Main exercise...

For each step, please write the prod code and accompanying test(s):

1. Define a val `lens` which is a lambda function of type `Extract<Request, String?>` that extracts the "foobar" header from an http4k Request.
2. Extract the `lens` val into a function `optional()` that returns an `Extract<Request, String?>` for a passed Header name.
3. Define a `MyHeader` object and move the `optional()` onto it.
4. Create `required()` in `MyHeader` which returns an `Extract<Request, String>` that extracts the header and throws `ExtractFailed` if it is null.
5. Define an object `MyQuery` that targets the query parameters instead of headers.
6. Extract out a common superclass `Builder` which has a constructor with a val `get` of type `(String, Request) -> String?`. `MyHeader` and `MyQuery` will extend `Builder`. Pull `optional()` and `required()` up to `Builder`.
7. Generify `Builder` to be expressed in `ENTITY` and `PART` instead of `Request` and `String`.
8. Add a guard to the `get` calls to catch any thrown Exceptions and turn these into `ExtractionFailed`.
9. Implement `map()` in `Builder` with the following signature:
```kotlin
fun <NEXT_PART> map(nextGet: (PART) -> NEXT_PART): Builder<ENTITY, NEXT_PART> = TODO()
```
... that transforms `PART` into a new type.

10. Implement an extension method `fun <T> Builder<T, String>.int() : Builder<T, Int>` that maps the `String` to an `Int`.

### Bonus exercise:
11. Introduce an interface `BiDi<ENTITY, PART>` into `definitions.kt` that encapsulates both injecting and extracting:
    ```interface BiDi<ENTITY, PART> : Extract<ENTITY, PART>, Inject<PART, ENTITY>```
    Notice that the above is impossible due to a clash of types! Rewrite `Extract` and `Inject` to be interfaces with a single `invoke()` function.
12. Rewrite `Builder` to return instances of `BiDi`. Just make the injection methods `{ TODO() }` for now
13. Introduce a `set` function val in to the `Builder` constructor with the following signature:
```kotlin
val set: (String, ENTITY, PART) -> ENTITY = TODO()
```
Implement the injection methods on `BiDi` and reimplement `map()` signature to also take 2 mapping functions:
```kotlin
fun <NEXT_PART> map(nextGet: (PART) -> NEXT_PART, nextSet: (NEXT_PART) -> PART): Builder<ENTITY, NEXT_PART> = TODO()
```
### Bonus (bonus) exercise:
14. Convert the get and set mapping functions in `Builder` to extract/inject lists of values. Reimplement the extract and inject functions to use the first value in the list.
15. Add `multi` as a member of `Builder`, which extracts/injects the entire list of values.


# Postfix humour from the Haskell community...

(it's not this bad - I promise! :) )

<img src="https://pbs.twimg.com/media/B58DjdCCQAASQrI.jpg" alt="lenses"/>

