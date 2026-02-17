import { apiServer } from "../lib/api-server";
import {
  adaptCommentDTO,
  adaptIssueDetailDTO,
  adaptIssueFeedDTO,
} from "./adapter";
import { CommentPageResponse, IssueCardProps, IssuePageResponse } from "./type";
import { CommentData, IssueDetailData, IssueDetailDTO } from "./type";

export async function getIssueFeed(): Promise<IssueCardProps[]> {
  try {
    const data = await apiServer.get<IssuePageResponse>("api/v1/issues");

    return data.content.map(adaptIssueFeedDTO);
  } catch (error) {
    console.error("Error fetching feed:", error);
    return [];
  }
}

export async function getIssueById(issueId: number): Promise<IssueDetailData> {
  try {
    const data = await apiServer.get<IssueDetailDTO>(
      `api/v1/issues/${issueId}`,
    );
    return adaptIssueDetailDTO(data);
  } catch (error) {
    console.error(`Error fetching issue ${issueId}:`, error);
    throw error;
  }
}

// TODO: unify comments and replies response types
export async function getCommentsByIssueId(
  issueId: number,
): Promise<CommentData[]> {
  try {
    const data = await apiServer.get<CommentPageResponse>(
      `api/v1/issues/${issueId}/comments`,
    );
    return data.content.map(adaptCommentDTO);
  } catch (error) {
    console.error(`Error fetching comments for issue ${issueId}:`, error);
    throw error;
  }
}

export async function getIssueUpdatesByIdMock(issueId: number): Promise<any> {
  return [
    {
      id: 1,
      title: "Official Site Inspection",
      desc: "An engineer from the PWD visited the site this morning. They have marked the area with yellow tape.",
      date: "Yesterday",
      type: "official",
      author: "PWD Engineer",
      likes: 45,
      isMajor: true,
      images: [
        "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?q=80&w=1000",
      ],
      comments: [
        {
          id: 1,
          authorName: "Resident A",
          content: "I saw them, they left at 2 PM.",
          createdAt: "1h",
          likes: 2,
          replies: [
            {
              id: 2,
              authorName: "Arjun Singh",
              content: "Thanks! Please share it.",
              createdAt: "4h ago",
              likes: 1,
            },
          ],
        },
        {
          id: 3,
          authorName: "Official Rep",
          content: "We will return tomorrow with more equipment.",
          createdAt: "30m",
          likes: 5,
          replies: [],
        },
        {
          id: 4,
          authorName: "Resident A",
          content: "I saw them, they left at 2 PM.",
          createdAt: "1h",
          likes: 2,
          replies: [
            {
              id: 5,
              authorName: "Arjun Singh",
              content: "Thanks! Please share it.",
              createdAt: "4h ago",
              likes: 1,
            },
          ],
        },
        {
          id: 6,
          authorName: "Official Rep",
          content: "We will return tomorrow with more equipment.",
          createdAt: "30m",
          likes: 5,
          replies: [],
        },
        {
          id: 7,
          authorName: "Resident A",
          content: "I saw them, they left at 2 PM.",
          createdAt: "1h",
          likes: 2,
          replies: [
            {
              id: 8,
              authorName: "Arjun Singh",
              content: "Thanks! Please share it.",
              createdAt: "4h ago",
              likes: 1,
            },
          ],
        },
        {
          id: 9,
          authorName: "Official Rep",
          content: "We will return tomorrow with more equipment.",
          createdAt: "30m",
          likes: 5,
          replies: [],
        },
      ],
    },
    {
      id: 10,
      title: "Reality Check: No Work Yet",
      desc: "It has been 24 hours since the inspection, but no machinery has arrived yet. Traffic is still bad.",
      date: "2 hours ago",
      type: "citizen",
      author: "Ravi Kumar",
      likes: 12,
      isMajor: false,
      images: [
        "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?q=80&w=1000",
        "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?q=80&w=1000",
      ],
    },
  ];
}
