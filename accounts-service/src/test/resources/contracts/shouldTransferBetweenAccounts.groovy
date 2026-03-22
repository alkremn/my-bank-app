import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should transfer funds between accounts"
    request {
        method POST()
        url "/accounts/transfer"
        headers {
            contentType applicationJson()
        }
        body(
                fromLogin: "ivanov",
                toLogin: "petrov",
                amount: 300
        )
    }
    response {
        status 200
    }
}
