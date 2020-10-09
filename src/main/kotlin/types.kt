// Return an PART which has been extracted from ENTITY:
typealias Extract<ENTITY, PART> = (ENTITY) -> PART

// Return a copy of ENTITY with PART injected into it:
typealias Inject<PART, ENTITY> = (PART, ENTITY) -> ENTITY

class ExtractFailed(val name: String) : Exception()
