package com.research.assistant;

import lombok.Data;

@Data //will create getter setter wherever req
public class ResearchRequest {
       private String content;
       private String operation; //i.e whether to perform summarise functionality , or suggesting relate topics fucntionality
}
