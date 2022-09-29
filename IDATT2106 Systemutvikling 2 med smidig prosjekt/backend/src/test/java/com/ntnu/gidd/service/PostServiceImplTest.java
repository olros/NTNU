package com.ntnu.gidd.service;


import com.ntnu.gidd.config.ModelMapperConfig;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.post.PostCreateDto;
import com.ntnu.gidd.dto.post.PostDto;
import com.ntnu.gidd.factories.ActivityFactory;
import com.ntnu.gidd.factories.PostFactory;
import com.ntnu.gidd.factories.UserFactory;
import com.ntnu.gidd.model.Post;
import com.ntnu.gidd.repository.ActivityRepository;
import com.ntnu.gidd.repository.PostRepository;
import com.ntnu.gidd.repository.UserRepository;
import com.ntnu.gidd.service.post.PostServiceImpl;
import com.ntnu.gidd.service.rating.PostLikeService;
import com.ntnu.gidd.utils.JpaUtils;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ModelMapperConfig.class)
@SpringBootTest
public class PostServiceImplTest {

    @Spy
    @Autowired
    ModelMapper modelMapper;

    @Mock
    UserRepository userRepository;


    @Mock
    ActivityRepository activityRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    PostLikeService postLikeService;

    @InjectMocks
    PostServiceImpl postService;

    ModelMapper usemodelMapper = new ModelMapper();



    ActivityFactory activityFactory = new ActivityFactory();

    UserFactory userFactory = new UserFactory();

    PostFactory postFactory = new PostFactory();

    private Post post;

    private Predicate predicate;
    private Pageable pageable;

    @BeforeEach
    public void setup() throws Exception {
        post = postFactory.getObject();

        assert post != null;
        lenient().when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        predicate = JpaUtils.getEmptyPredicate();
        pageable = JpaUtils.getDefaultPageable();
    }

    @Test
    public void testPostServiceGetAll(){
        List<Post> postList = List.of(post);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());
        Page<PostDto> posters = postPage.map(s -> usemodelMapper.map(s , PostDto.class));
        lenient().when(postRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(postPage);
        lenient().when(postLikeService.checkListLikes(any(Page.class), any(String.class)))
                .thenReturn(posters);
        Page<PostDto> actualPosts = postService.findAllPosts(predicate, pageable, post.getCreator().getEmail());

        assertThat(postPage.getContent().size()).isEqualTo(actualPosts.getContent().size());
        for (int i = 0; i < postList.size() ; i++) {
            assertThat(postList.get(i).getId()).isEqualTo(actualPosts.getContent().get(i).getId());
        }
    }

    @Test
    public void testPostServiceGetByID(){
        PostDto actualPost = postService.getPostById(post.getId(), "");
        lenient().when(postLikeService.hasLiked(any(String.class), any()))
                .thenReturn(false);
        assertThat(actualPost.getId()).isEqualTo(post.getId());
    }

    @Test
    public void testPostServiceCreate(){
        PostCreateDto newPost = PostCreateDto.builder().activityId(post.getActivity().getId())
                .content(post.getContent())
                .image(getRandomString(11))
                .build();
        when(activityRepository.findById(post.getActivity().getId())).thenReturn(Optional.of(post.getActivity()));
        when(userRepository.findByEmail(post.getCreator().getEmail())).thenReturn(Optional.of(post.getCreator()));
        when(postRepository.save(any())).thenReturn(post);
        PostDto actualPost = postService.savePost(newPost, post.getCreator().getEmail());
        assertThat(actualPost.getContent()).isEqualTo(post.getContent());
    }
    @Test
    public void testPostServiceUpdate(){
        String newContent = getRandomString(22);
        PostDto updatePost = PostDto.builder().content(newContent).build();
        lenient().when(activityRepository.findById(post.getActivity().getId())).thenReturn(Optional.of(post.getActivity()));
        lenient().when(postRepository.save(any())).thenReturn(post);
        lenient().when(postLikeService.hasLiked(any(String.class), any()))
                .thenReturn(false);
        PostDto actualPost = postService.updatePost(post.getId(), updatePost, "");
        assertThat(updatePost.getContent()).isEqualTo(post.getContent());
    }

    @Test
    public void testPostServiceDelete(){
        postService.deletePost(post.getId());
    }
}
