package com.example.simple_blog.domain.comment.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts/{postId}/comments")
class CommentController {
}