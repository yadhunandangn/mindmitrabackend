package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.Entity.Blog;
import com.MindMitra.Rolebased.Repository.BlogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepo blogRepo;

    public Blog createBlog(Blog blog) {
        return blogRepo.save(blog);
    }

    public void deleteBlog(Long id) {
        if (!blogRepo.existsById(id)) {
            throw new RuntimeException("Blog not found with id: " + id);
        }
        blogRepo.deleteById(id);
    }
}