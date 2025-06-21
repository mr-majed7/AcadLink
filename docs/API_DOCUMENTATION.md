<!-- Generator: Widdershins v4.0.1 -->

<h1 id="">AcadLink</h1>

A platform to manage and share academic resources

Base URLs:

* <a href="http://localhost:8080/v1">http://localhost:8080/v1</a>

# Authentication

* HTTP Authentication, scheme: bearer

<details>
<summary><h2>Jump to Section</h2></summary>

* [Public](#-1-public)
* [Email Verification](#-2-email-verification)
* [Profile Management](#-3-profile-management)
* [Folder Management](#-4-folder-management)
* [Materials Management](#-5-materials-management)
* [Find Materials](#-6-find-materials)
* [Peer Management](#-7-peer-management)
* [Schemas](#schemas)

</details>

<br>

---

<h1 id="-1-public">1. Public</h1>

Public endpoints to Sign Up, Sign In

## signUp

<a id="opIdsignUp"></a>

> Code samples

`POST /public/sign-up`

*Sign up a new user*

> Body parameter

```json
{
  "firstName": "string",
  "lastName": "string",
  "institute": "string",
  "email": "string",
  "userName": "string",
  "password": "string"
}
```

<h3 id="signup-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UserSignUpDTO](#schemausersignupdto)|true|none|

> Example responses

> 200 Response

<h3 id="signup-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="signup-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## logIn

<a id="opIdlogIn"></a>

> Code samples

`POST /public/login`

*Log in a user*

> Body parameter

```json
{
  "usernameorEmail": "string",
  "password": "string"
}
```

<h3 id="login-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UserLoginDTO](#schemauserlogindto)|true|none|

> Example responses

> 200 Response

<h3 id="login-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseString](#schemaapiresponsestring)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## checkUsername

<a id="opIdcheckUsername"></a>

> Code samples

`GET /public/check-username/{userName}`

*Check if username exists*

<h3 id="checkusername-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|userName|path|string|true|none|

> Example responses

> 200 Response

<h3 id="checkusername-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|boolean|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="-2-email-verification">2. Email Verification</h1>

Endpoints for email verification and OTP management

## verifyEmail

<a id="opIdverifyEmail"></a>

> Code samples

`POST /auth/verify-email`

*Verify email address*

Verifies a user's email address using the provided OTP code. The OTP code must be entered within 5 minutes of being sent.

> Body parameter

```json
{
  "email": "string",
  "otp": "string"
}
```

<h3 id="verifyemail-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[EmailVerificationRequest](#schemaemailverificationrequest)|true|none|

> Example responses

> 200 Response

<h3 id="verifyemail-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Email verified successfully|[EmailVerificationResponse](#schemaemailverificationresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Invalid verification code or user not found|[EmailVerificationResponse](#schemaemailverificationresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## resendVerification

<a id="opIdresendVerification"></a>

> Code samples

`POST /auth/resend-verification`

*Resend verification code*

Resends the verification code to the user's email address. Use this endpoint if the original code expired or was not received.

<h3 id="resendverification-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|email|query|string|true|Email address to resend verification code to|

> Example responses

> 200 Response

<h3 id="resendverification-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Verification code sent successfully|[EmailVerificationResponse](#schemaemailverificationresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|User not found or email already verified|[EmailVerificationResponse](#schemaemailverificationresponse)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="-3-profile-management">3. Profile Management</h1>

Endpoints for managing user profiles

## getUser

<a id="opIdgetUser"></a>

> Code samples

`GET /user/get-user`

> Example responses

> 200 Response

<h3 id="getuser-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseUserResponseDTO](#schemaapiresponseuserresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="-4-folder-management">4. Folder Management</h1>

Endpoints for managing folders

## updateFolder

<a id="opIdupdateFolder"></a>

> Code samples

`PUT /folder/update-folder/{folderId}`

*Update a specific folder by ID*

> Body parameter

```json
{
  "name": "string",
  "privacy": "PUBLIC"
}
```

<h3 id="updatefolder-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|folderId|path|string(uuid)|true|none|
|body|body|[FolderCreateDTO](#schemafoldercreatedto)|true|none|

> Example responses

> 200 Response

<h3 id="updatefolder-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseUpdateFolderResponseDTO](#schemaapiresponseupdatefolderresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## createFolder

<a id="opIdcreateFolder"></a>

> Code samples

`POST /folder/create`

*Create a new folder*

> Body parameter

```json
{
  "name": "string",
  "privacy": "PUBLIC"
}
```

<h3 id="createfolder-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[FolderCreateDTO](#schemafoldercreatedto)|true|none|

> Example responses

> 200 Response

<h3 id="createfolder-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAllFolderResponseDTO](#schemaapiresponseallfolderresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getFolder

<a id="opIdgetFolder"></a>

> Code samples

`GET /folder/get-folder/{folderId}`

*Get a specific folder by ID*

<h3 id="getfolder-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|folderId|path|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="getfolder-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseFolderResponseDTO](#schemaapiresponsefolderresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getAllFolders

<a id="opIdgetAllFolders"></a>

> Code samples

`GET /folder/get-all`

*Get all folders*

> Example responses

> 200 Response

<h3 id="getallfolders-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListAllFolderResponseDTO](#schemaapiresponselistallfolderresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="-5-materials-management">5. Materials Management</h1>

Endpoints for managing materials

## updateMaterial

<a id="opIdupdateMaterial"></a>

> Code samples

`PUT /material/update-material/{id}`

*Update material by ID*

> Body parameter

```yaml
name: string
folderId: 5b6379a4-2a6c-4085-b184-45838a3b8e7e
type: BOOK
link: string
file: string
privacy: PUBLIC

```

<h3 id="updatematerial-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|string(uuid)|true|none|
|body|body|[MaterialAddDTO](#schemamaterialadddto)|false|none|

> Example responses

> 200 Response

<h3 id="updatematerial-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMaterialResponseDTO](#schemaapiresponsematerialresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## addMaterials

<a id="opIdaddMaterials"></a>

> Code samples

`POST /material/add-material`

*Add a new material*

> Body parameter

```yaml
name: string
folderId: 5b6379a4-2a6c-4085-b184-45838a3b8e7e
type: BOOK
link: string
file: string
privacy: PUBLIC

```

<h3 id="addmaterials-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[MaterialAddDTO](#schemamaterialadddto)|false|none|

> Example responses

> 200 Response

<h3 id="addmaterials-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMaterialResponseDTO](#schemaapiresponsematerialresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getMaterialByType

<a id="opIdgetMaterialByType"></a>

> Code samples

`GET /material/get-materials-by-type/{type}/{folder-id}`

*Get materials by type and folder ID*

<h3 id="getmaterialbytype-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|type|path|string|true|none|
|folder-id|path|string(uuid)|true|none|

#### Enumerated Values

|Parameter|Value|
|---|---|
|type|BOOK|
|type|LECTURE_SLIDE|
|type|LECTURE_NOTE|
|type|OTHER|

> Example responses

> 200 Response

<h3 id="getmaterialbytype-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListMaterialResponseDTO](#schemaapiresponselistmaterialresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getMaterial

<a id="opIdgetMaterial"></a>

> Code samples

`GET /material/get-material-by-id/{id}`

*Get material by ID*

<h3 id="getmaterial-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="getmaterial-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMaterialResponseDTO](#schemaapiresponsematerialresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## deleteMaterial

<a id="opIddeleteMaterial"></a>

> Code samples

`DELETE /material/delete-material/{id}`

*Delete material by ID*

<h3 id="deletematerial-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="deletematerial-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseBoolean](#schemaapiresponseboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="-6-find-materials">6. Find Materials</h1>

Endpoints for finding materials

## viewPeersMaterials

<a id="opIdviewPeersMaterials"></a>

> Code samples

`GET /view-peers-materials/{peers-user-id}`

*View Materilas of Peers*

<h3 id="viewpeersmaterials-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|peers-user-id|path|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="viewpeersmaterials-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListMaterialResponseDTO](#schemaapiresponselistmaterialresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## searchMaterials

<a id="opIdsearchMaterials"></a>

> Code samples

`GET /search-materials`

*Search Materials with keywords*

<h3 id="searchmaterials-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|keyWords|query|string|true|none|

> Example responses

> 200 Response

<h3 id="searchmaterials-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListMaterialResponseDTO](#schemaapiresponselistmaterialresponsedto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="-7-peer-management">7. Peer Management</h1>

Endpoints to manage peers and search for users

## acceptRequest

<a id="opIdacceptRequest"></a>

> Code samples

`PUT /peers/accept-peer-request/{reqId}`

*Accept peer request*

<h3 id="acceptrequest-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|reqId|path|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="acceptrequest-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseBoolean](#schemaapiresponseboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## addPeer

<a id="opIdaddPeer"></a>

> Code samples

`POST /peers/send-peer-request/{userId}`

*Send peer request*

<h3 id="addpeer-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|userId|path|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="addpeer-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseBoolean](#schemaapiresponseboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## searchUsers

<a id="opIdsearchUsers"></a>

> Code samples

`GET /peers/search-user/{entry}`

*Search users*

<h3 id="searchusers-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|entry|path|string|true|none|

> Example responses

> 200 Response

<h3 id="searchusers-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListSearchResultDTO](#schemaapiresponselistsearchresultdto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getPeers

<a id="opIdgetPeers"></a>

> Code samples

`GET /peers/get-peers`

*Get peers*

> Example responses

> 200 Response

<h3 id="getpeers-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListPeerInfoDTO](#schemaapiresponselistpeerinfodto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getRequests

<a id="opIdgetRequests"></a>

> Code samples

`GET /peers/get-peer-requests/{type}`

*Get peer requests*

<h3 id="getrequests-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|type|path|string|true|none|

#### Enumerated Values

|Parameter|Value|
|---|---|
|type|SENT|
|type|RECEIVED|

> Example responses

> 200 Response

<h3 id="getrequests-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListPeerInfoDTO](#schemaapiresponselistpeerinfodto)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## removePeer

<a id="opIdremovePeer"></a>

> Code samples

`DELETE /peers/remove-peer/{peerId}`

*Remove peer*

<h3 id="removepeer-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|peerId|path|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="removepeer-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseBoolean](#schemaapiresponseboolean)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

# Schemas

<h2 id="tocS_ApiResponseBoolean">ApiResponseBoolean</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseboolean"></a>
<a id="schema_ApiResponseBoolean"></a>
<a id="tocSapiresponseboolean"></a>
<a id="tocsapiresponseboolean"></a>

```json
{
  "data": true,
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|boolean|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_MaterialAddDTO">MaterialAddDTO</h2>
<!-- backwards compatibility -->
<a id="schemamaterialadddto"></a>
<a id="schema_MaterialAddDTO"></a>
<a id="tocSmaterialadddto"></a>
<a id="tocsmaterialadddto"></a>

```json
{
  "name": "string",
  "folderId": "5b6379a4-2a6c-4085-b184-45838a3b8e7e",
  "type": "BOOK",
  "link": "string",
  "file": "string",
  "privacy": "PUBLIC"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|false|none|none|
|folderId|string(uuid)|false|none|none|
|type|string|false|none|none|
|link|string|false|none|none|
|file|string(binary)|false|none|none|
|privacy|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|type|BOOK|
|type|LECTURE_SLIDE|
|type|LECTURE_NOTE|
|type|OTHER|
|privacy|PUBLIC|
|privacy|PRIVATE|
|privacy|FRIENDS|
|privacy|PEERS|
|privacy|INSTITUTIONAL|

<h2 id="tocS_ApiResponseMaterialResponseDTO">ApiResponseMaterialResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsematerialresponsedto"></a>
<a id="schema_ApiResponseMaterialResponseDTO"></a>
<a id="tocSapiresponsematerialresponsedto"></a>
<a id="tocsapiresponsematerialresponsedto"></a>

```json
{
  "data": {
    "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
    "name": "string",
    "link": "string",
    "type": "BOOK",
    "privacy": "PUBLIC",
    "folderId": "5b6379a4-2a6c-4085-b184-45838a3b8e7e"
  },
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[MaterialResponseDTO](#schemamaterialresponsedto)|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_MaterialResponseDTO">MaterialResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemamaterialresponsedto"></a>
<a id="schema_MaterialResponseDTO"></a>
<a id="tocSmaterialresponsedto"></a>
<a id="tocsmaterialresponsedto"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "name": "string",
  "link": "string",
  "type": "BOOK",
  "privacy": "PUBLIC",
  "folderId": "5b6379a4-2a6c-4085-b184-45838a3b8e7e"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|false|none|none|
|name|string|false|none|none|
|link|string|false|none|none|
|type|string|false|none|none|
|privacy|string|false|none|none|
|folderId|string(uuid)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|type|BOOK|
|type|LECTURE_SLIDE|
|type|LECTURE_NOTE|
|type|OTHER|
|privacy|PUBLIC|
|privacy|PRIVATE|
|privacy|FRIENDS|
|privacy|PEERS|
|privacy|INSTITUTIONAL|

<h2 id="tocS_FolderCreateDTO">FolderCreateDTO</h2>
<!-- backwards compatibility -->
<a id="schemafoldercreatedto"></a>
<a id="schema_FolderCreateDTO"></a>
<a id="tocSfoldercreatedto"></a>
<a id="tocsfoldercreatedto"></a>

```json
{
  "name": "string",
  "privacy": "PUBLIC"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|false|none|none|
|privacy|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|privacy|PUBLIC|
|privacy|PRIVATE|
|privacy|FRIENDS|
|privacy|PEERS|
|privacy|INSTITUTIONAL|

<h2 id="tocS_ApiResponseUpdateFolderResponseDTO">ApiResponseUpdateFolderResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseupdatefolderresponsedto"></a>
<a id="schema_ApiResponseUpdateFolderResponseDTO"></a>
<a id="tocSapiresponseupdatefolderresponsedto"></a>
<a id="tocsapiresponseupdatefolderresponsedto"></a>

```json
{
  "data": {
    "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
    "name": "string",
    "createdAt": "2019-08-24",
    "privacy": "PUBLIC"
  },
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[UpdateFolderResponseDTO](#schemaupdatefolderresponsedto)|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_UpdateFolderResponseDTO">UpdateFolderResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaupdatefolderresponsedto"></a>
<a id="schema_UpdateFolderResponseDTO"></a>
<a id="tocSupdatefolderresponsedto"></a>
<a id="tocsupdatefolderresponsedto"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "name": "string",
  "createdAt": "2019-08-24",
  "privacy": "PUBLIC"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|false|none|none|
|name|string|false|none|none|
|createdAt|string(date)|false|none|none|
|privacy|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|privacy|PUBLIC|
|privacy|PRIVATE|
|privacy|FRIENDS|
|privacy|PEERS|
|privacy|INSTITUTIONAL|

<h2 id="tocS_UserSignUpDTO">UserSignUpDTO</h2>
<!-- backwards compatibility -->
<a id="schemausersignupdto"></a>
<a id="schema_UserSignUpDTO"></a>
<a id="tocSusersignupdto"></a>
<a id="tocsusersignupdto"></a>

```json
{
  "firstName": "string",
  "lastName": "string",
  "institute": "string",
  "email": "string",
  "userName": "string",
  "password": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|firstName|string|false|none|none|
|lastName|string|false|none|none|
|institute|string|false|none|none|
|email|string|false|none|none|
|userName|string|false|none|none|
|password|string|false|none|none|

<h2 id="tocS_UserLoginDTO">UserLoginDTO</h2>
<!-- backwards compatibility -->
<a id="schemauserlogindto"></a>
<a id="schema_UserLoginDTO"></a>
<a id="tocSuserlogindto"></a>
<a id="tocsuserlogindto"></a>

```json
{
  "usernameorEmail": "string",
  "password": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|usernameorEmail|string|false|none|none|
|password|string|false|none|none|

<h2 id="tocS_ApiResponseString">ApiResponseString</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsestring"></a>
<a id="schema_ApiResponseString"></a>
<a id="tocSapiresponsestring"></a>
<a id="tocsapiresponsestring"></a>

```json
{
  "data": "string",
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|string|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_AllFolderResponseDTO">AllFolderResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaallfolderresponsedto"></a>
<a id="schema_AllFolderResponseDTO"></a>
<a id="tocSallfolderresponsedto"></a>
<a id="tocsallfolderresponsedto"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "name": "string",
  "createdAt": "2019-08-24",
  "privacy": "PUBLIC"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|false|none|none|
|name|string|false|none|none|
|createdAt|string(date)|false|none|none|
|privacy|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|privacy|PUBLIC|
|privacy|PRIVATE|
|privacy|FRIENDS|
|privacy|PEERS|
|privacy|INSTITUTIONAL|

<h2 id="tocS_ApiResponseAllFolderResponseDTO">ApiResponseAllFolderResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseallfolderresponsedto"></a>
<a id="schema_ApiResponseAllFolderResponseDTO"></a>
<a id="tocSapiresponseallfolderresponsedto"></a>
<a id="tocsapiresponseallfolderresponsedto"></a>

```json
{
  "data": {
    "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
    "name": "string",
    "createdAt": "2019-08-24",
    "privacy": "PUBLIC"
  },
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[AllFolderResponseDTO](#schemaallfolderresponsedto)|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_EmailVerificationRequest">EmailVerificationRequest</h2>
<!-- backwards compatibility -->
<a id="schemaemailverificationrequest"></a>
<a id="schema_EmailVerificationRequest"></a>
<a id="tocSemailverificationrequest"></a>
<a id="tocsemailverificationrequest"></a>

```json
{
  "email": "string",
  "otp": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|email|string|true|none|none|
|otp|string|true|none|none|

<h2 id="tocS_EmailVerificationResponse">EmailVerificationResponse</h2>
<!-- backwards compatibility -->
<a id="schemaemailverificationresponse"></a>
<a id="schema_EmailVerificationResponse"></a>
<a id="tocSemailverificationresponse"></a>
<a id="tocsemailverificationresponse"></a>

```json
{
  "verified": true,
  "message": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|verified|boolean|false|none|none|
|message|string|false|none|none|

<h2 id="tocS_ApiResponseListMaterialResponseDTO">ApiResponseListMaterialResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistmaterialresponsedto"></a>
<a id="schema_ApiResponseListMaterialResponseDTO"></a>
<a id="tocSapiresponselistmaterialresponsedto"></a>
<a id="tocsapiresponselistmaterialresponsedto"></a>

```json
{
  "data": [
    {
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
      "name": "string",
      "link": "string",
      "type": "BOOK",
      "privacy": "PUBLIC",
      "folderId": "5b6379a4-2a6c-4085-b184-45838a3b8e7e"
    }
  ],
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[[MaterialResponseDTO](#schemamaterialresponsedto)]|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_ApiResponseUserResponseDTO">ApiResponseUserResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseuserresponsedto"></a>
<a id="schema_ApiResponseUserResponseDTO"></a>
<a id="tocSapiresponseuserresponsedto"></a>
<a id="tocsapiresponseuserresponsedto"></a>

```json
{
  "data": {
    "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
    "firstName": "string",
    "lastName": "string",
    "institute": "string",
    "email": "string",
    "userName": "string",
    "createdAt": "2019-08-24"
  },
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[UserResponseDTO](#schemauserresponsedto)|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_UserResponseDTO">UserResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemauserresponsedto"></a>
<a id="schema_UserResponseDTO"></a>
<a id="tocSuserresponsedto"></a>
<a id="tocsuserresponsedto"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "firstName": "string",
  "lastName": "string",
  "institute": "string",
  "email": "string",
  "userName": "string",
  "createdAt": "2019-08-24"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|false|none|none|
|firstName|string|false|none|none|
|lastName|string|false|none|none|
|institute|string|false|none|none|
|email|string|false|none|none|
|userName|string|false|none|none|
|createdAt|string(date)|false|none|none|

<h2 id="tocS_ApiResponseListSearchResultDTO">ApiResponseListSearchResultDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistsearchresultdto"></a>
<a id="schema_ApiResponseListSearchResultDTO"></a>
<a id="tocSapiresponselistsearchresultdto"></a>
<a id="tocsapiresponselistsearchresultdto"></a>

```json
{
  "data": [
    {
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
      "firstName": "string",
      "lastName": "string",
      "email": "string",
      "username": "string",
      "institute": "string",
      "peerStatus": "FALSE"
    }
  ],
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[[SearchResultDTO](#schemasearchresultdto)]|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_SearchResultDTO">SearchResultDTO</h2>
<!-- backwards compatibility -->
<a id="schemasearchresultdto"></a>
<a id="schema_SearchResultDTO"></a>
<a id="tocSsearchresultdto"></a>
<a id="tocssearchresultdto"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "username": "string",
  "institute": "string",
  "peerStatus": "FALSE"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|false|none|none|
|firstName|string|false|none|none|
|lastName|string|false|none|none|
|email|string|false|none|none|
|username|string|false|none|none|
|institute|string|false|none|none|
|peerStatus|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|peerStatus|FALSE|
|peerStatus|PENDING|
|peerStatus|ACCEPTED|

<h2 id="tocS_ApiResponseListPeerInfoDTO">ApiResponseListPeerInfoDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistpeerinfodto"></a>
<a id="schema_ApiResponseListPeerInfoDTO"></a>
<a id="tocSapiresponselistpeerinfodto"></a>
<a id="tocsapiresponselistpeerinfodto"></a>

```json
{
  "data": [
    {
      "reqId": "67d08b9d-044d-425e-b826-128502ab892f",
      "userId": "2c4a230c-5085-4924-a3e1-25fb4fc5965b",
      "firstName": "string",
      "lastName": "string",
      "email": "string",
      "institute": "string",
      "userName": "string"
    }
  ],
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[[PeerInfoDTO](#schemapeerinfodto)]|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_PeerInfoDTO">PeerInfoDTO</h2>
<!-- backwards compatibility -->
<a id="schemapeerinfodto"></a>
<a id="schema_PeerInfoDTO"></a>
<a id="tocSpeerinfodto"></a>
<a id="tocspeerinfodto"></a>

```json
{
  "reqId": "67d08b9d-044d-425e-b826-128502ab892f",
  "userId": "2c4a230c-5085-4924-a3e1-25fb4fc5965b",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "institute": "string",
  "userName": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|reqId|string(uuid)|false|none|none|
|userId|string(uuid)|false|none|none|
|firstName|string|false|none|none|
|lastName|string|false|none|none|
|email|string|false|none|none|
|institute|string|false|none|none|
|userName|string|false|none|none|

<h2 id="tocS_ApiResponseFolderResponseDTO">ApiResponseFolderResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsefolderresponsedto"></a>
<a id="schema_ApiResponseFolderResponseDTO"></a>
<a id="tocSapiresponsefolderresponsedto"></a>
<a id="tocsapiresponsefolderresponsedto"></a>

```json
{
  "data": {
    "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
    "name": "string",
    "createdAt": "2019-08-24",
    "privacy": "PUBLIC",
    "materials": [
      {
        "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
        "name": "string",
        "link": "string",
        "type": "BOOK",
        "privacy": "PUBLIC",
        "folderId": "5b6379a4-2a6c-4085-b184-45838a3b8e7e"
      }
    ]
  },
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[FolderResponseDTO](#schemafolderresponsedto)|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|

<h2 id="tocS_FolderResponseDTO">FolderResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemafolderresponsedto"></a>
<a id="schema_FolderResponseDTO"></a>
<a id="tocSfolderresponsedto"></a>
<a id="tocsfolderresponsedto"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "name": "string",
  "createdAt": "2019-08-24",
  "privacy": "PUBLIC",
  "materials": [
    {
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
      "name": "string",
      "link": "string",
      "type": "BOOK",
      "privacy": "PUBLIC",
      "folderId": "5b6379a4-2a6c-4085-b184-45838a3b8e7e"
    }
  ]
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|false|none|none|
|name|string|false|none|none|
|createdAt|string(date)|false|none|none|
|privacy|string|false|none|none|
|materials|[[MaterialResponseDTO](#schemamaterialresponsedto)]|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|privacy|PUBLIC|
|privacy|PRIVATE|
|privacy|FRIENDS|
|privacy|PEERS|
|privacy|INSTITUTIONAL|

<h2 id="tocS_ApiResponseListAllFolderResponseDTO">ApiResponseListAllFolderResponseDTO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistallfolderresponsedto"></a>
<a id="schema_ApiResponseListAllFolderResponseDTO"></a>
<a id="tocSapiresponselistallfolderresponsedto"></a>
<a id="tocsapiresponselistallfolderresponsedto"></a>

```json
{
  "data": [
    {
      "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
      "name": "string",
      "createdAt": "2019-08-24",
      "privacy": "PUBLIC"
    }
  ],
  "error": "string",
  "status": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|data|[[AllFolderResponseDTO](#schemaallfolderresponsedto)]|false|none|none|
|error|string|false|none|none|
|status|integer(int32)|false|none|none|
