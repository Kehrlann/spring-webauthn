---
theme: default
class: 'text-center'
highlighter: shiki
lineNumbers: true
transition: none
# use UnoCSS
css: unocss
aspectRatio: "16/9"
colorSchema: "light"
---

# Implementing WebAuthN

<br>

### Josh Long
### Daniel Garnier-Moiroux

Devoxx France, 2024-04-18


---
layout: two-cols
class: two-cols-right smaller
---

#### Josh
### Long

🥑️ Advocate extraordinaire

<br>
<br>

<logos-spring-icon /> Spring
<br>
<logos-twitter /> @starbuxman
<br>
📩️ josh@joshlong.com

::right::

#### Daniel
### Garnier-Moiroux

Security nerd 🤓️

<br>

Spring + Tanzu <logos-spring-icon />
<br>
@Kehrlann@hachyderm.io <logos-mastodon-icon />
<br>
@Kehrlann <logos-twitter />
<br>
<span style="margin-left: -60px;">contact@garnier.wf 📩️</span>



---
layout: center
---

# What is this **WebAuthN** thing?

---
class: smaller
---

# Glossary

- 🌍 **WebAuthN**
    - [W3C spec](https://www.w3.org/TR/webauthn-3/) -> Javascript in your browser
- 📱 **Authenticator**
    - _A cryptographic entity, hardware or software_ -> thing to authenticate with
- 🔑 **Passkey**
    - A _discoverable_ WebAuthN authenticator
- 🐶 **FIDO2**
    - **F**ast **ID**entity **O**nline, by FIDO alliance: Apple, Google, Microsoft
    - Two specs: WebAuthN + CTAP

---
layout: two-cols-header
---

## How does it work?

<div v-click>

Using *asymmetric cryptography*,

i.e. [private + public] key pair

</div>

---
layout: image
image: /asymmetric-crypto-1.png
---

---
layout: image
image: /asymmetric-crypto-2.png
---

---
layout: image
image: /asymmetric-crypto-3.png
---

---
layout: image
image: /asymmetric-crypto-4.png
---

---
layout: two-cols-header
---

## How does it work?

Using *asymmetric cryptography*,

i.e. [private + public] key pair

::left::

<div v-click>

### 🛂📒 Registration

Generate key pair, per *origin*
<br>
(~ per domain name)
<br>
Save private key, share public key

</div>

::right::

<div v-click>

### 🔓🔑 Authentication

Use private key to prove identity

</div>

---

## Example: registration

<br>

<img src="/webauthn-registration-flow-01.svg" />

---
layout: image
image: /webauthn-registration-1.png
---

---
layout: image
image: /webauthn-registration-2.png
---

---
layout: image
image: /webauthn-registration-3.png
---

---
layout: image
image: /webauthn-registration-4.png
---

---
layout: image
image: /webauthn-registration-5.png
---

---
layout: image
image: /webauthn-registration-6.png
---

---
layout: center
---

# ~~ Let's code!

<img src="/lets-code.gif" style="height: 400px"/>


---
layout: image
image: /fido-attestation-structures.svg
---

---

# Observations

- 🤯 It's Complicated™
- 👷 Use a library
    - (It's _still_ complicated)
- 🤔 Define your use-case(s)
- 📚 Read up!
    - Yubico's site is a gold mine
- 🍃🔐 Coming Soon™ in Spring Security

---
class: small-padding
---

Demo: <br>**https://github.com/Kehrlann/spring-webauthn**

Spring Security: <br>**https://github.com/rwinch/spring-security-webauthn**

<!-- ouch the hack -->
<div style="float:right; margin-right: 50px; text-align: center;">
  <img src="/qr-code.png" style="margin-bottom: -45px; margin-top: -15px;" >
</div>

Daniel:
- <logos-mastodon-icon /> @Kehrlann@hachyderm.io
- <logos-twitter /> @Kehrlann

Josh:
- <logos-twitter /> @starbuxman

---
layout: image
hideInToc: true
image: /meet-me.jpg
class: end
---

# **Merci 😊**

