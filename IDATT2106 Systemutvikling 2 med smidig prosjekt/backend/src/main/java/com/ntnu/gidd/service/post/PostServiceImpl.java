package com.ntnu.gidd.service.post;


import com.ntnu.gidd.dto.post.PostCreateDto;
import com.ntnu.gidd.dto.post.PostDto;
import com.ntnu.gidd.exception.ActivityNotFoundException;
import com.ntnu.gidd.exception.PostNotFoundExecption;
import com.ntnu.gidd.exception.UserNotFoundException;
import com.ntnu.gidd.model.Activity;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.model.QPost;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.PostRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.rating.PostLikeService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of a post service
 */
@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService{

    PostRepository postRepository;

    ActivityRepository activityRepository;

    UserRepository userRepository;

    PostLikeService postLikeService;

    ModelMapper modelMapper;

    /**
     * Method to get all posts for a logged in users feed
     * @param predicate
     * @param pageable
     * @param email
     * @return list of posts relevant to the user
     */
    @Override
    public Page<PostDto> findAllPosts(Predicate predicate, Pageable pageable, String email) {
        QPost post = QPost.post;
        predicate = ExpressionUtils.and(predicate, post.creator.email.eq(email).or(post.creator.followers.any().email.eq(email)));
        Page<PostDto> postDtos = postRepository.findAll(predicate, pageable).map(s -> modelMapper.map(s, PostDto.class));
        return postLikeService.checkListLikes(postDtos, email);
    }

    /**
     * Method to get a post by id
     * @param postId
     * @param email
     * @return the requested post
     */
    @Override
    public PostDto getPostById(UUID postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);
        PostDto postDto = modelMapper.map(post, PostDto.class);
        postDto.setHasLiked(postLikeService.hasLiked(email, postDto.getId()));
        return postDto;
    }

    /**
     * Method to create a new post
     * @param post
     * @param email
     * @return the saved post
     */
    @Override
    public PostDto savePost(PostCreateDto post, String email) {
        Post newPost = Post.builder().id(UUID.randomUUID()).content(post.getContent()).image(post.getImage()).build();
        if (post.getActivityId() != null) {
            Activity activity = activityRepository.findById(post.getActivityId()).orElse(null);
            newPost.setActivity(activity);
        } else {
            newPost.setActivity(null);
        }
        newPost.setComments(List.of());
        newPost.setCreator(userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new));
        newPost.setLikes(List.of());
        newPost = postRepository.save(newPost);
        PostDto postDto = modelMapper.map(newPost, PostDto.class);
        postDto.setHasLiked(postLikeService.hasLiked(email, postDto.getId()));
        return postDto;
    }

    /**
     * Method to update a post by id
     * @param postId
     * @param updatePost
     * @param email
     * @return the updated post
     */
    @Override
    public PostDto updatePost(UUID postId, PostDto updatePost, String email) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);
        post.setContent(updatePost.getContent());
        post.setImage(updatePost.getImage());
        if(updatePost.getActivity() == null) post.setActivity(null);
        else post.setActivity(activityRepository.findById(updatePost.getActivity().getId()).orElseThrow(ActivityNotFoundException::new));

        PostDto postDto = modelMapper.map(postRepository.save(post), PostDto.class);
        postDto.setHasLiked(postLikeService.hasLiked(email, postId));
        return postDto;
    }

    /**
     * Method to delete a post by id
     * @param postId
     */
    @Override
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundExecption::new);
        postRepository.delete(post);

    }

    /**
     * Methos to get all liked posts by a user by id
     * @param predicate
     * @param pageable
     * @param id
     * @return list of liked posts
     */
    public Page<PostDto> getPostsLikes(Predicate predicate, Pageable pageable, UUID id){
        QPost post = QPost.post;
        predicate = ExpressionUtils.allOf(predicate, post.likes.any().id.eq(id));
        Page<PostDto> posts = this.postRepository.findAll(predicate, pageable)
                .map(s -> modelMapper.map(s, PostDto.class));

        return postLikeService.checkListLikes(posts,id);
    }
    /**
     * Methos to get all liked posts by a user by email
     * @param predicate
     * @param pageable
     * @param email
     * @return list of liked posts
     */
    public Page<PostDto> getPostsLikes(Predicate predicate, Pageable pageable, String email){
        QPost post = QPost.post;
        predicate = ExpressionUtils.allOf(predicate, post.likes.any().email.eq(email));
        Page<PostDto> posts = this.postRepository.findAll(predicate, pageable)
                .map(s -> modelMapper.map(s, PostDto.class));

        return postLikeService.checkListLikes(posts,email);
    }
}
