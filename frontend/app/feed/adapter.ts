import { apiClient } from "../lib/api";
import { getCookies } from "../lib/cookie-utils";
import { IssueCardData, IssueFeedDTO, IssuePageResponse } from "./type";

const getStableRandom = (seed: number) => {
  const x = Math.sin(seed) * 10000;
  return x - Math.floor(x);
};

export function adaptIssueFeedDTO(dto: IssueFeedDTO): IssueCardData {
  const seed = getStableRandom(dto.id);
  // Randomly pick a severity based on ID
  const severities = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];
  const severity = severities[Math.floor(seed * severities.length)];

  // Randomly pick a category
  const categories = [
    "INFRASTRUCTURE",
    "SANITATION",
    "ELECTRICITY",
    "PUBLIC SAFETY",
  ];
  const category = categories[Math.floor(seed * categories.length)];

  const mockAddress =
    seed > 0.5 ? "Sector 20, Gurugram" : "Cyber City, DLF Phase 2";
  const mockDesc = `This is a reported issue regarding ${dto.title.toLowerCase()}... (ID #${dto.id})`;
  const mockComments = Math.floor(seed * 20);

  return {
    id: dto.id,
    authorId: dto.author_id,
    title: dto.title,
    desc: mockDesc,
    category: category,
    authorName: dto.author_name,
    authorImageUrl: dto.author_image_url,
    createdAt: dto.created_at,
    status: dto.status,
    isResolved: dto.status === "RESOLVED",
    severity: severity,
    votes: dto.vote_count,
    comments: mockComments,
    images: dto.media_urls,
    address: mockAddress,
    hasVoted: dto.has_user_voted,
    canVote: dto.can_user_vote,
  };
}

export async function getIssueFeed(): Promise<IssueCardData[]> {
  try {
    const headers = await getCookies();
    const data = await apiClient.get<IssuePageResponse>(
      "api/v1/issues",
      headers,
    );
    console.log(`Fetched ${data.content.length} issues`);

    return data.content.map(adaptIssueFeedDTO);
  } catch (error) {
    console.error("Error fetching feed:", error);
    return [];
  }
}
