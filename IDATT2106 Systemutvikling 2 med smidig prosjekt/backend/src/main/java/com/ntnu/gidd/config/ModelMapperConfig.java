package com.ntnu.gidd.config;

import com.ntnu.gidd.dto.Activity.ActivityDto;
import com.ntnu.gidd.dto.Activity.ActivityListDto;
import com.ntnu.gidd.dto.User.ContextAwareUserDto;
import com.ntnu.gidd.model.Registration;
import com.ntnu.gidd.util.ContextAwareModelMapper;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ContextAwareModelMapper();

        modelMapper
                .getConfiguration()
                .setFieldMatchingEnabled(true)
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        modelMapper.createTypeMap(Registration.class, ActivityListDto.class)
                .addMapping(src ->src.getActivity().getId(), ActivityListDto::setId)
                .addMapping(src -> src.getActivity().getTrainingLevel().getLevel(), ActivityListDto::setLevel);
        modelMapper.createTypeMap(Registration.class, ActivityDto.class)
                .addMapping(src ->src.getActivity().getId(), ActivityDto::setId)
                .addMapping(src -> src.getActivity().getTrainingLevel().getLevel(), ActivityDto::setLevel);

        return modelMapper;
    }
}