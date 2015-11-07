# ssh-tls-client 

[![Circle CI](https://circleci.com/gh/spotify/ssh-tls.svg?style=shield)](https://circleci.com/gh/spotify/ssh-tls)

A Java library that does a TLS handshake by using SSH keys.

If you want to test the validator, run ValidatorJerseyMain and issue the following curl line:
`curl -XPOST --upload-file fun/ssh-tls-client/validator/src/test/resources/test_alice.crt -v http://localhost:62911/_auth`