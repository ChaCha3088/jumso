package kr.co.jumso.dummy

data class TemporaryCompany(
    val id: Int,
    val name: String,
    val alias: String?,
    val color: String,
    val emails: List<String>,
    val enabled: Boolean,
    val scale: String?,
    val order: String?,
    val logo_url: String?,
)

data class Data(
    val companies: List<TemporaryCompany>,
    val last_page: Boolean,
    val current_page: Int,
    val next_page: Int,
    val total_pages: Int,
)

data class TemporaryResponse(
    val result: Int,
    val data: Data,
)
