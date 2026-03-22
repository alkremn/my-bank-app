import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return account by login"
    request {
        method GET()
        url "/accounts/ivanov"
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body(
                id: $(producer(anyNumber())),
                login: "ivanov",
                name: "Ivan Ivanov",
                birthdate: "1990-01-15",
                balance: 1000
        )
    }
}
