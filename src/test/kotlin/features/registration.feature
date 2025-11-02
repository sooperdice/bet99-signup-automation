@registration
Feature: Registration

  Background:
    Given the user is on the home page
    And they dismiss any jurisdiction or cookie popups
    And they tap the Join button

  @happy_path @regression
  Scenario: User completes registration successfully
    Given the user is on Join Step 1
    When they complete Step 1 with valid details
    And they complete Step 2 with a valid address
    And they submit the registration
    Then the account is created successfully


  @negative @wip
  Scenario: User cannot proceed with missing mandatory fields
    Given the user is on Join Step 1
    When they attempt to continue without filling any details
    Then validation errors are shown for mandatory fields
    And the user remains on Join Step 1

  @negative @wip
  Scenario: User cannot complete registration with an invalid address
    Given the user is on Join Step 1
    When they complete Step 1 with valid details
    And they attempt to enter an invalid address on Step 2
    Then an address error is displayed
    And the account is not created


  @negative @wip
  Scenario Outline: User cannot proceed with invalid or missing input
    Given the user is on Join Step 1
    When they enter "<email>" and "<username>"
    And they try to continue
    Then an error message should appear indicating invalid input

    Examples:
      | email              | username  |
      | invalidemail.com   | testuser1 |
      | user@              | testuser2 |
      |                    | testuser3 |


  @negative @blocklist @wip
  Scenario: Blocklisted user cannot register
    Given the user is on Join Step 1
    When they enter blocklisted First Name, Last Name, and Date of Birth
    Then an error message should indicate the user is not allowed to register
    When they enter blocklisted First Name, Last Name, and Postal Code
    Then an error message should indicate the user is not allowed to register

