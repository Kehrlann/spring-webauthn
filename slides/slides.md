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
<br>

### Daniel Garnier-Moiroux

Voxxed Zürich, 2024-03-07


---
layout: image-right
image: /vdz.jpg
class: smaller
---

#### Daniel
### Garnier-Moiroux
<br>

Software Engineer

- <logos-spring-icon /> Broadcom+Tanzu+Spring
- <logos-mastodon-icon /> @Kehrlann@hachyderm.io
- <logos-twitter /> @Kehrlann
- <logos-firefox /> https://garnier.wf/
- <logos-github-icon /> github.com/Kehrlann/
- <fluent-emoji-flat-envelope-with-arrow /> daniel.garnier-moiroux@broadcom.com

---
layout: image-left
image: /vdz-sponsors.png
class: dark-background
background-size: contain
---

# Sponsors !

<br />

# 😍🎉🙌

---
layout: center
---

# What is this **WebAuthN** thing?

---
layout: two-cols-header
---

# **FIDO2 is ...**

::left::

## 🌍📄 WebAuthN

JavaScript in your browser

(W3C)

::right::

## 💻🔑 CTAP

How your browsers or OS talks to hardware authenticators

(FIDO alliance)

---

# What about **Passkeys**?

<br>

> Passkeys are **discoverable** WebAuthN credentials
>
> <br>
>
> -- *https://passkeys.io/*

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

# References

<br>

### **https://github.com/Kehrlann/sso-live-coding**

<!-- ouch the hack -->
<!-- https://mobile.devoxx.com/events/dvbe23/talks/2943/details -->
<div style="float:right; margin-right: 50px; text-align: center;">
  <img src="/qr-code.png" style="margin-bottom: -45px; margin-top: -15px;" >
</div>

<br>

- <logos-mastodon-icon /> @Kehrlann@hachyderm.io
- <logos-twitter /> @Kehrlann
- <logos-firefox /> https://garnier.wf/
- <fluent-emoji-flat-envelope-with-arrow /> daniel.garnier-moiroux@broadcom.com


---
layout: image
hideInToc: true
image: /meet-me.jpg
class: end
---

# **Merci 😊**

