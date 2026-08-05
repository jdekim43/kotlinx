# Kotlin Utilities

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-blue.svg)](https://kotlinlang.org/)
[![Version](https://maven-badges.sml.io/maven-central/kim.jade/kotlinx/badge.svg)](https://github.com/jdekim43/kotlinx)

## About

Kotlin Utilities is a modular Kotlin Multiplatform library that provides reusable building blocks for application
development. It offers a consistent API across JVM, JavaScript, and Kotlin/Native so common code can use everyday
utilities, data encoders, cryptographic helpers, and structured logging without duplicating platform-specific
implementations.

## Feature

* annotations
    * @Experimental
    * @InDevelopment
* encoder
    * Hex
    * Base64
    * Base58
    * ULEB
    * BCS (experimental)
* security
    * thirdparty library wrapper
        * [whyoleg/cryptography-kotlin](https://github.com/whyoleg/cryptography-kotlin)
        * [KotlinCrypto](https://github.com/KotlinCrypto)
    * hash
        * sha1
        * sha2
        * sha3
        * keccak
        * shake
        * cshake
        * parallelhash
        * tuplehash
        * blake2b
        * blake2s
        * and more...
    * mac
        * hmac
        * and more...
    * encryption
        * aes
        * rsa
        * ec
        * and more...

## License

Distributed under the APACHE 2.0 License. See LICENSE for more information.
