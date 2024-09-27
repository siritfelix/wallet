INSERT INTO
    users (FIRST_NAME, LAST_NAME, EMAIL)
VALUES
    ('Felix', 'Sirit', 'name@gmail.com'),
    ('Felix', 'Sirit', 'name2@gmail.com');

INSERT INTO
    wallets (ACCOUNT_ID, BALANCE, USER_ID)
VALUES
    ('263e934f-0db5-41d8-9f0a-d2a810d63b31', 1000, 1),
    ('263e934f-0db5-41d8-9f0a-d2a810d63b32', 0, 2);

INSERT INTO
    movements (
        AMOUNT,
        BALANCE,
        DATE,
        DEBIT_ACCOUNT_BANK,
        DESTINATION_WALLET,
        TRANSACTION_ID,
        TRANSACTION_TYPE,
        WALLET_ID
    )
VALUES
    (
        1000,
        1000,
        '2024-09-27 01:07:30.860932',
        '123456789',
        '263e934f-0db5-41d8-9f0a-d2a810d63b31',
        'b3378238-1959-4b0a-8f5d-cf161ef9580a',
        'DEPOSIT',
        1
    );