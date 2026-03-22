import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should update account balance"
    request {
        method POST()
        url "/accounts/ivanov/balance"
        headers {
            contentType applicationJson()
        }
        body(
                amount: 500
        )
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
                balance: 1500
        )
    }
}
