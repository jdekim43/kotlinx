# Kotlin Utilities

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-blue.svg)](https://kotlinlang.org/)
[![Version](https://maven-badges.sml.io/maven-central/kim.jade/kotlinx/badge.svg)](https://central.sonatype.com/artifact/kim.jade/kotlinx)

A modular Kotlin Multiplatform utility library for JVM, JavaScript, and Kotlin/Native. It provides common extensions and DSLs, Hex/Base64/Base58 encoding, ULEB and experimental BCS serialization, digest and HMAC helpers, and Base58Check.

> This document reflects the public declarations and implementations currently in this repository. Known behavior and limitations are noted where relevant.

## Modules

| Gradle artifact | Purpose |
| --- | --- |
| `kim.jade:kotlinx` | Common extension functions, DSLs, exception messages, object pools, and console, environment, and thread utilities |
| `kim.jade:kotlinx-encoding` | `Hex`, `Base64`, `Base58`, `ULEB`, and experimental BCS serialization |
| `kim.jade:kotlinx-security` | Digest and HMAC extensions, `Base58Check`, additional digest providers, and legacy hash and low-level ECDSA APIs |

The encoding module depends internally on the core module, while the security module depends on both core and encoding. If your source code imports types from another module directly, add an explicit dependency on that module as well.

## Supported targets

| Family | Targets                                                                                                |
| --- |-------------------------------------------------------------------------------------------------------|
| JVM | JVM 11+                                                                                               |
| JavaScript | Browser, Node.js, ES2015                                                                              |
| Apple | macOS arm64, iOS arm64/x64/simulator arm64, watchOS arm64/simulator arm64, tvOS arm64/simulator arm64 |
| Desktop | Linux x64/arm64, MinGW x64                                                                            |

Wasm and macOS x64 targets are not configured.

## Installation

### Kotlin Multiplatform

Add only the modules you need to `commonMain`.

```kotlin
repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("kim.jade:kotlinx:3.0.0-beta.1")
            implementation("kim.jade:kotlinx-encoding:3.0.0-beta.1")
            implementation("kim.jade:kotlinx-security:3.0.0-beta.1")
        }
    }
}
```

### JVM

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("kim.jade:kotlinx:3.0.0-beta.1")
    implementation("kim.jade:kotlinx-encoding:3.0.0-beta.1")
    implementation("kim.jade:kotlinx-security:3.0.0-beta.1")
}
```

## Quick start

```kotlin
import dev.whyoleg.cryptography.algorithms.SHA256
import kim.jade.encoding.Base64
import kim.jade.encoding.Hex
import kim.jade.encoding.decode
import kim.jade.encoding.encode
import kim.jade.kotlinx.extension.utf8
import kim.jade.security.crypto.hash

val source = "Hello, Kotlin!"
val bytes = source.utf8()

val hex = bytes.encode(Hex)
val base64 = bytes.encode(Base64)
val restored = base64.decode(Base64).utf8()
val sha256 = source.hash(SHA256, Hex)

check(hex == "48656C6C6F2C204B6F746C696E21")
check(base64 == "SGVsbG8sIEtvdGxpbiE=")
check(restored == source)
check(sha256.length == 64)
```

## Using `kotlinx`

### Strings and value presence

```kotlin
import kim.jade.kotlinx.extension.*

val bytes: ByteArray = "Hello".utf8()
val text: String = bytes.utf8()

val name: String? = "  "
check(name.hasNotValue())
check(name.hasValue(blankIsValue = true))

val items: List<Int>? = listOf(1, 2)
check(items.hasValue())
check(true.toBinaryInt() == 1)
check(0.toBoolean().not())
```

| Function | Behavior |
| --- | --- |
| `String.utf8()` | Converts a string to a UTF-8 `ByteArray`. |
| `ByteArray.utf8()` | Converts UTF-8 bytes to a string. |
| `String?.hasValue(blankIsValue = false)` | Returns `true` when the value is not null, empty, or blank. With `blankIsValue=true`, a blank string counts as a value. |
| `String?.hasNotValue(blankIsValue = false)` | The inverse of `hasValue`. |
| `Collection<T>?.hasValue()` / `hasNotValue()` | Tests whether a collection is null or empty. |
| `Boolean.toBinaryInt()` | Converts `true` to 1 and `false` to 0. |
| `Int.toBoolean()` | Converts only 0 to `false`; every other value becomes `true`. |

### Collections

```kotlin
import kim.jade.kotlinx.extension.*

val groups = listOf("A", "A", "B", "A")
    .sequentialGroupBy { it }
// [(A, [A, A]), (B, [B]), (A, [A])]

val labels = listOf(1, 2, 3)
    .withValue(mapOf(1 to "one", 3 to "three")) { key -> "missing:$key" }
    .toList()
// [one, missing:2, three]
```

| Function | Behavior |
| --- | --- |
| `List<T>.sequentialGroupBy(keySelector)` | Creates a `Pair<K, List<T>>` for each consecutive run of the same key. Unlike `groupBy`, it does not merge separated items. Inputs whose first key is null are not currently supported. |
| `Iterable<K>.withValue(values: Map<K, V>, defaultValue)` | Replaces each key with its mapped value and creates a default value for missing keys. |
| `Iterable<K>.withValue(values: Iterable<V>, keySelector, defaultValue)` | Merges a value stream sorted in the same order as the keys, filling in missing values. |

Use the `Iterable` overload only when the values form a sorted subset of the keys, contain at most one value per key, and do not fall outside the first and last key. Values remaining after the keys are exhausted are still emitted. A leading value with no matching key can be returned repeatedly or prevent iteration from terminating.

### Numbers and byte arrays

```kotlin
import kim.jade.kotlinx.extension.*
import kim.jade.kotlinx.extension.max as utilityMax
import kim.jade.kotlinx.extension.min as utilityMin

check(2.dayToHour() == 48)
check(3L.minuteToSecond() == 180L)
check(utilityMin(3, 7) == 3)
check(utilityMax("a", "z") == "z")

val bigEndian = 0x01020304.toByteArray(ByteOrder.BIG_ENDIAN)
check(bigEndian.contentEquals(byteArrayOf(1, 2, 3, 4)))
check(bigEndian.toInt(ByteOrder.BIG_ENDIAN) == 0x01020304)

val packet = ByteArray(6)
packet.write(offset = 0, value = 0xCAFE.toUShort())
packet.write(offset = 2, value = 0x01020304)

val leftPadded = byteArrayOf(1, 2).padStart(4)
val binary = 5.toByte().toBinary() // false, false, false, false, false, true, false, true

val hexAlphabet = "0123456789ABCDEF".toCharArray()
check(255.toString(hexAlphabet, radix = 16) == "FF")
```

| Group | Functions and rules |
| --- | --- |
| Time units | `dayToHour`, `hourToMinute`, `minuteToSecond`, and `secondToMillisecond` for `Int` and `Long`. These are simple multiplications and do not check for overflow. |
| Comparison | `<T : Comparable<T>> min(a, b)` / `max(a, b)` |
| Integer to bytes | `Short`, `UShort`, `Int`, `UInt`, `Long`, and `ULong` each provide `toByteArray(ByteOrder)`. The default is big-endian. |
| Bytes to integer | `ByteArray.toShort/toUShort/toInt/toUInt/toLong/toULong(ByteOrder)`. Pass exactly 2, 4, or 8 bytes as required by the target type. |
| Padding | `ByteArray.padStart(size, byte)` / `padEnd(size, byte)`. `size` must be at least the original array size. |
| Writing | `ByteArray.write(offset, value)` provides overloads for `Byte`, `UByte`, `ByteArray`, `Short`, `UShort`, `Int`, `UInt`, `Long`, and `ULong`. Multi-byte overloads default `littleEndian` to `false`. |
| Binary conversion | `Byte.toBinary`, `Int.toBinary`, `ByteArray.toBinary`, and `BooleanArray.toByte/toInt/toByteArray`. A `BooleanArray` passed to `toByteArray` must contain a multiple of eight elements. The current implementation works correctly only with an empty or eight-element array; larger arrays fail with an indexing error. |
| Custom radix | `toString(characters, radix)` for `Int`, `UInt`, `Long`, and `ULong` uses a custom alphabet. The radix must be at least 2 and no larger than the alphabet; signed values must be non-negative. Zero currently produces an empty string. |

### Running coroutines in parallel

```kotlin
import kim.jade.kotlinx.extension.parallel

suspend fun loadPage() {
    val (user, posts) = parallel(
        block1 = { loadUser() },
        block2 = { loadPosts() },
    )

    val threeResults = parallel({ "A" }, { 1 }, { true })
    val manyResults: List<String> = parallel(
        { "A" },
        { "B" },
        { "C" },
        { "D" },
        { "E" },
        { "F" },
    )
}
```

`parallel` requires `kotlinx-coroutines-core`. Add `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0` explicitly if your project does not already provide it; the JVM artifact does not publish this dependency transitively.

### URL strings

```kotlin
import kim.jade.kotlinx.extension.*

val added = "https://example.com/items"
    .addQueryParameter("page", "1")
// https://example.com/items?page=1

val replaced = added.putQueryParameter("page", "2")
// https://example.com/items?page=2

check("undefined".escapeJsNull() == null)
check("NULL".escapeJsNull() == null)
```

| Function | Behavior |
| --- | --- |
| `String.addQueryParameter(key, value)` | Appends a raw `key=value` after `?` or `&`. |
| `String.putQueryParameter(key, value)` | Replaces the first existing value for the key, or appends it if absent. |
| `String?.escapeJsNull()` | Converts case-insensitive `"null"` and `"undefined"` strings to Kotlin null. Null and blank strings are returned unchanged. |

These functions do not perform URL percent-encoding or reposition fragments. In addition, `putQueryParameter` does not enforce key boundaries, so `page` can incorrectly match the prefix of `pageSize`. Encode external input before calling these functions, and use a dedicated URL parser when a URL may contain similarly named keys.

### Exceptions and localized display messages

```kotlin
import kim.jade.kotlinx.exception.*

val ko = Locale.from("ko-KR")
val en = Locale.from(language = "en", region = "US")

val resolver = StaticExceptionDisplayMessageResolver().apply {
    setMessages(
        code = "USER_NOT_FOUND",
        messages = mapOf(
            ko to "사용자를 찾을 수 없습니다.",
            en to "User not found.",
        ),
    )
}

ExceptionDisplayMessageResolver.defaultLocale = en
ExceptionDisplayMessageResolver.use(resolver)

val exception = AppException(
    code = "USER_NOT_FOUND",
    level = ExceptionLevel.WARNING,
    data = mapOf("userId" to "42"),
)

check(exception.message == "[USER_NOT_FOUND] AppException")
check(exception.getDisplayMessage(ko) == "사용자를 찾을 수 없습니다.")
```

| API | Behavior |
| --- | --- |
| `Locale.from(language, region, script)` | Creates a `Locale` in `ll-RR` or `ll-Ssss-RR` form. Language and region must be two characters; script must be four. |
| `Locale.from(value)` | Parses strings such as `ko-KR` and `zh-Hans-CN`. |
| `AppException(...)` | Stores a code, cause, level, and additional data, and formats the message as `[code] message`. |
| `AppException.getDisplayMessage(locale)` | Resolves a user-facing message through the global resolver. |
| `ExceptionDisplayMessageResolver.use(resolver)` | Replaces the global resolver. |
| `ExceptionDisplayMessageResolver.getMessage(code/exception, locale)` | Resolves the message associated with a code or exception. |
| `loadDefaultResolver()` | Creates the platform's default resolver. The companion object normally calls it automatically, so you generally do not need to call it yourself. |
| `StaticExceptionDisplayMessageResolver.setMessages(...)` | Merges messages from a complete map, a locale map for one code, or a code map for one locale. |

Message fallback order is the requested locale, `defaultLocale`, the first message for the code, and finally the built-in English message. The current `ExceptionLevel` constants are `FETAL`, `ERROR`, and `WARNING`. `FETAL` is the actual public name and must be used as written.

`ErrorCode` is a type alias for `String`. `StaticExceptionDisplayMessageResolver.DEFAULT_KEY` is `"default"`, the default message key for a general `Throwable`.

### Object pools

```kotlin
import kim.jade.kotlinx.pool.ObjectPool

val builders = ObjectPool { StringBuilder() }

val value = builders.use { builder ->
    builder.clear().append("pooled").toString()
}

val builder = builders.acquire()
try {
    builder.clear()
} finally {
    builders.release(builder)
}

builders.close()
```

| Function | Behavior |
| --- | --- |
| `acquire()` | Takes one stored object, or creates one with `newInstance` when the pool is empty. |
| `use(body)` | Borrows an object and returns it automatically from a `finally` block. |
| `release(obj)` | Returns an object to the pool. The caller must prevent duplicate releases of the same object. |
| `close()` | Clears all currently stored references. The pool can still be used afterward. |

No capacity limit or object-cleanup callback is provided.

### Arguments, regular expressions, environment, and threads

```kotlin
import kim.jade.kotlinx.extension.qualifiedOrSimpleName
import kim.jade.kotlinx.io.environmentVariable
import kim.jade.kotlinx.io.eprintln
import kim.jade.kotlinx.io.isTTY
import kim.jade.kotlinx.regex.Regexes
import kim.jade.kotlinx.thread.ThreadLocal
import kim.jade.kotlinx.thread.currentThreadName
import kim.jade.kotlinx.thread.value
import kim.jade.kotlinx.util.parseArguments

val arguments = parseArguments(
    "--port=8080",
    "-v",
    "--tag=a",
    "--tag=b",
)
// {port=[8080], v=[], tag=[a, b]} (the actual value of v is one empty string)

check(Regexes.EMAIL.matches("dev@example.com"))
check(Regexes.HTTP_URL.matches("https://example.com"))

val path = environmentVariable("PATH")
eprintln("Printed to stderr")
val ansiEnabled = isTTY()

val local = ThreadLocal<String>()
local.value = "request-1"
check(local.get() == "request-1")
local.remove()

val threadName = currentThreadName()
val className = String::class.qualifiedOrSimpleName
```

| API | Behavior |
| --- | --- |
| `parseArguments(vararg args)` | Reads dash-prefixed options with an optional `=value` into a `Map<String, List<String>>`. Repeated keys accumulate, valueless flags use `""`, and positional arguments are ignored. Everything after the first `=` remains in the value. |
| `Regexes.EMAIL` / `HTTP_URL` | Predefined `Regex` values for checking email addresses and HTTP(S) URLs. They are not comprehensive parsers for every international address or URL. |
| `environmentVariable(name)` | Uses `System.getenv` on JVM, `process.env` on Node.js, and `getenv` on Native. The JS implementation depends on the Node API and cannot be used in a browser without a polyfill. |
| `eprintln(text)` | Prints to stderr on JVM/Native or to `console.error` on JS. |
| `isTTY()` | Uses `System.console() != null` on JVM and `isatty` for stdout on Native; always returns `false` on JS. |
| `ThreadLocal.get/set/remove` | Manages thread-local values on JVM/Native. On JS, it is a single value holder inside the object and provides no thread isolation. The `value` extension property can also read and write the value. |
| `currentThreadName()` | Returns the current name on JVM and null on JS/Native. |
| `KClass.qualifiedOrSimpleName` | Falls back through the qualified, canonical, and simple names when available. |

`Arguments` is a type alias for `Map<String, List<String>>`. `Quadra` and `Penta` can also be instantiated directly as general-purpose groups of four or five values, not only as coroutine results.

### JVM-only APIs

#### `java.util.Properties` extensions

```kotlin
import kim.jade.kotlinx.extension.*
import java.io.File
import java.util.Properties

val properties = Properties().apply {
    load(File("app.properties"), includeSystemProperties = true)
}

val port = properties.getInt("port", 8080)
val debug = properties.getBoolean("debug", false)

properties.loadFromSystem(
    environmentVariables = true,
    properties = true,
)

properties.loadFileBasedProperties(File("config"), prefix = "app.")
```

| Function | Behavior |
| --- | --- |
| `getBoolean/getShort/getInt/getLong/getFloat/getDouble/getString(key)` | Returns null when the property is absent and throws a conversion exception for malformed numbers. |
| `(key, defaultValue)` overloads of the same functions | Return the default value when the property is absent. |
| `load(vararg files, includeSystemProperties)` | Merges readable files in order, optionally loading system values first. |
| `load(inputStreams)` | Reads multiple `InputStream` instances in order and closes each one. |
| `loadFileBasedProperties(directory, prefix)` | Traverses a directory tree, storing each file's contents under a key formed by joining its relative path with dots. |
| `loadFromSystem(environmentVariables, properties)` | Merges environment variables and JVM system properties. |

#### JVM exceptions and shutdown hooks

```kotlin
import kim.jade.kotlinx.exception.*
import kim.jade.kotlinx.util.addShutdownHook
import java.io.File

val resolver = ResourceExceptionDisplayMessageResolver().apply {
    load(Locale.from("ko-KR"), File("messages/ko-KR.properties"))
}
ExceptionDisplayMessageResolver.use(resolver)

val combined = CompositeException(
    IllegalStateException("first"),
    IllegalArgumentException("second"),
)
check(combined.size == 2)

addShutdownHook { println("JVM shutting down") }
```

`ResourceExceptionDisplayMessageResolver.load` provides overloads for a resource path, a directory, `(Locale, File)`, `(Locale, InputStream)`, and `(Locale, Properties)`. The default resource path is `DEFAULT_DIRECTORY_PATH`, which is `"messages.exception"`. Prefer an overload that specifies the locale: directory loading currently parses the full filename, so a conventional name such as `ko-KR.properties` is skipped. `CompositeException` flattens nested composites, removes duplicate exceptions, and prints every cause from `printStackTrace`. `ExceptionOverview.fillInStackTrace()` skips creating a stack trace for the overview itself.

### JavaScript-only APIs

```kotlin
import kim.jade.kotlinx.extension.toInt8Array
import kim.jade.kotlinx.extension.toUint8Array

val bytes = byteArrayOf(1, 2, 3)
val signed = bytes.toInt8Array()
val unsigned = bytes.toUint8Array()

@OptIn(ExperimentalUnsignedTypes::class)
fun unsignedView(bytes: UByteArray) = bytes.toUint8Array()
```

Both `ByteArray?` and `UByteArray?` provide `toInt8Array()` and `toUint8Array()`, and `Buffer` is a type alias for `Uint8Array<ArrayBuffer>`. The implementation uses zero-copy `unsafeCast` operations and views, so do not pass a null receiver and assume that converted values may share the same backing buffer. `environmentVariable` is included in the same JS artifact, but it depends on Node's `process.env` and is not a browser API.

### Opt-in annotations

```kotlin
import kim.jade.kotlinx.annotation.Experimental
import kim.jade.kotlinx.annotation.InDevelopment

@OptIn(Experimental::class)
fun useExperimentalApi() = Unit
```

`@Experimental` is a warning-level `@RequiresOptIn` marker, while `@InDevelopment` is error-level. The transaction API currently uses `@Experimental`; `@InDevelopment` is provided only as a marker declaration.

## Using `kotlinx-encoding`

### `Encoder` and built-in encoders

```kotlin
import kim.jade.encoding.*

val raw = "Kotlin".encodeToByteArray()

val hex = Hex.encode(raw)
val hexByExtension = raw.encode(Hex)
check(hex == "4B6F746C696E")
check(hex.decode(Hex).contentEquals(raw))

val base64 = raw.encode(Base64)
check(base64 == "S290bGlu")
check(base64.decode(Base64).contentEquals(raw))

val base58 = raw.encode(Base58)
check(base58.decode(Base58).contentEquals(raw))
```

```kotlin
interface Encoder<OriginalType, EncodedType> {
    fun encode(data: OriginalType): EncodedType
    fun decode(data: EncodedType): OriginalType
}
```

| API | Behavior |
| --- | --- |
| `encoder.encode(original)` | Encodes the original value. |
| `encoder.decode(encoded)` | Decodes the encoded value back to its original form. |
| `original.encode(encoder)` | Extension-function form of `encoder.encode(original)`. |
| `encoded.decode(encoder)` | Extension-function form of `encoder.decode(encoded)`. |
| `EncoderException(cause)` | A common exception type available to encoder implementations. Errors from built-in encoders are not all wrapped in this type automatically. |

| Encoder | Format and input rules |
| --- | --- |
| `Hex` | Produces uppercase hexadecimal and accepts both uppercase and lowercase input. Decode input must have an even length. An odd-length input currently drops the final nibble silently instead of reporting an error, so validate it before decoding. |
| `Base64` | Uses the standard `A-Z a-z 0-9 + /` alphabet with `=` padding. Decoding ignores spaces, LF, and CR and is not a strict validator. |
| `Base58` | Uses the Bitcoin Base58 alphabet and preserves leading zero bytes as `1`. An invalid character causes a `NumberFormatException`. |

## Using `kotlinx-security`

### Digests and HMACs

```kotlin
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA256
import kim.jade.encoding.Hex
import kim.jade.security.crypto.hash
import kim.jade.security.crypto.verify

val digestBytes: ByteArray = "hello".hash(SHA256)
val digestHex: String = "hello".hash(SHA256, Hex)

val key = "secret".encodeToByteArray()
val mac = "hello".hash(
    algorithmId = HMAC,
    digest = SHA256,
    key = key,
)

check("hello".verify(HMAC, SHA256, key, mac))
check(digestBytes.size == 32)
check(digestHex.length == 64)
```

| Function | Result or behavior |
| --- | --- |
| `ByteArray.hash(digestId)` | Creates a digest `ByteArray` with `CryptographyProvider.Default`. |
| `String.hash(digestId)` | Converts the string to UTF-8 and creates its digest. |
| `ByteArray.hash(digestId, encoder)` | Encodes the digest directly with an encoder such as `Hex` or `Base64`. |
| `String.hash(digestId, encoder)` | Returns the UTF-8 digest encoded with the specified encoder. |
| `ByteArray.hash(HMAC, digestId, key)` | Creates an HMAC with the raw key. |
| `String.hash(HMAC, digestId, key)` | Creates an HMAC for the UTF-8 string. |
| `ByteArray.verify(HMAC, digestId, key, hash)` | Verifies an HMAC signature. |
| `String.verify(HMAC, digestId, key, hash)` | Verifies an HMAC signature for the UTF-8 string. |

All of these are blocking APIs, and `CryptographyProvider.Default` must support the selected algorithm. Use MD5 and SHA-1 only for compatibility, and use SHA-256 or stronger algorithms in new security designs. For password storage, use a dedicated password-hashing function or KDF with a salt and cost factor instead of a general-purpose digest or HMAC.

### Base58Check

```kotlin
import kim.jade.encoding.Base58Check
import kim.jade.encoding.Base58WithChecksum
import kim.jade.encoding.decode
import kim.jade.encoding.encode

val payload = byteArrayOf(0, 1, 2, 3)
val encoded = payload.encode(Base58Check)
val decoded = encoded.decode(Base58WithChecksum)

check(decoded.contentEquals(payload))
```

`Base58Check.encode` appends the first four bytes of a double-SHA-256 checksum to the payload, then encodes the result as Base58. `decode` validates the length and checksum and returns only the payload; it throws an exception on a mismatch. `Base58WithChecksum` is a type alias for `Base58Check`.

### Registering additional digest IDs and providers

Import standard IDs such as SHA-256 from `dev.whyoleg.cryptography.algorithms`. To use the additional Keccak, SHAKE, cSHAKE, ParallelHash, TupleHash, and BLAKE2 IDs with the modern `hash` extensions, register their providers during application initialization.

```kotlin
import dev.whyoleg.cryptography.CryptographySystem
import kim.jade.encoding.Hex
import kim.jade.security.crypto.KECCAK256
import kim.jade.security.crypto.hash
import kim.jade.security.crypto.provider.kotlincrypto.KotlinCryptoProvider
import kim.jade.security.crypto.provider.pure.PureKotlinProvider

fun initializeCryptography() {
    // Place the RIPEMD implementation before the bundled platform providers,
    // and the custom KotlinCrypto algorithms afterward as a fallback.
    CryptographySystem.registerProvider(
        lazyOf(PureKotlinProvider()),
        priority = 50,
    )
    CryptographySystem.registerProvider(
        lazyOf(KotlinCryptoProvider()),
        priority = 200,
    )
}

fun applicationStart() {
    initializeCryptography() // Call exactly once when the process starts.
    val keccak = "hello".hash(KECCAK256, Hex)
}
```

Register providers once per process and before any call to `hash`, `verify`, or `Base58Check`. Registration fails after the default provider has already been resolved. Keep `KotlinCryptoProvider` at a fallback priority so a vetted platform provider is selected first for standard HMAC algorithms.

Do not use the current `KotlinCryptoProvider` implementation for HMAC verification: it does not reject truncated or length-mismatched MACs. Until that verifier is fixed, register this provider only as a fallback for the additional digest algorithms.

| Additional ID | Constructor arguments |
| --- | --- |
| `SHA512t(t)` | `t` for SHA-512/t |
| `KECCAK224`, `KECCAK256`, `KECCAK384`, `KECCAK512` | Singletons |
| `SHAKE128(outputLength?)`, `SHAKE256(outputLength?)` | Optional output length in bytes |
| `CSHAKE128(N, S, outputLength?)`, `CSHAKE256(...)` | Function-name byte array, customization byte array, and optional output length in bytes |
| `ParallelHash128(S, B, outputLength?)`, `ParallelHash256(...)` | Customization, block size `B` in bytes, and optional output length in bytes |
| `TupleHash128(S, outputLength?)`, `TupleHash256(...)` | Customization and optional output length in bytes |
| `BLAKE2b(bitStrength)`, `BLAKE2s(bitStrength)` | Bit strength |

`KotlinCryptoProvider.name` is `"KotlinCrypto"`, and `getOrNull(id)` returns a supported implementation or null. Digests support standard MD5, SHA-1/2/3, and the additional IDs listed above. HMAC digests support only SHA-1, SHA-224/256/384/512, and SHA3-224/256/384/512; they do not support MD5, Keccak, SHAKE, or BLAKE algorithms. `PureKotlinProvider.name` is `"PureKotlin"`, and its `getOrNull(id)` currently provides only RIPEMD-160. Constructor validation occurs when the underlying KotlinCrypto algorithm is instantiated.

### Compatibility `HashFunction` API

An independent API is also provided for existing code that uses the `kr.jadekim.common.crypto.hash` package. Prefer the `kim.jade.security.crypto.hash` API described above for new code.

```kotlin
import kim.jade.encoding.Hex
import kr.jadekim.common.crypto.hash.HashFunction
import kr.jadekim.common.crypto.hash.SHA_256
import kr.jadekim.common.crypto.hash.hash

val digest = "hello".hash(SHA_256, Hex)

val reverseHash = HashFunction { bytes -> bytes.reversedArray() }
val reversed = byteArrayOf(1, 2, 3).hash(reverseHash)
check(reversed.contentEquals(byteArrayOf(3, 2, 1)))
```

| API | Behavior |
| --- | --- |
| `HashFunction.hash(ByteArray)` | The core digest function supplied by the implementation. |
| `HashFunction.hash(String)` | Converts a string to UTF-8 and delegates to the core function. |
| `HashFunction.hash(data, encoder)` | Encodes a `ByteArray` or `String` digest with the specified encoder. |
| `HashFunction { bytes -> ... }` | Creates an implementation from a lambda and wraps lambda failures in `HashException`. |
| `ByteArray.hash(function)` / `String.hash(function)` | Receiver-style shortcuts. Overloads that take an encoder are also available. |
| `HashException(cause)` | The common exception for lambda-based hash implementations. |

Predefined values are `MD5`, `SHA_1`, `SHA_224`, `SHA_256`, `SHA_384`, `SHA_512`, `KECCAK_224`, `KECCAK_256`, `KECCAK_384`, `KECCAK_512`, `SHA3_224`, `SHA3_256`, `SHA3_384`, `SHA3_512`, and `RIPEMD160`.

## License

Distributed under the Apache License 2.0. See [LICENSE](LICENSE) for details.
