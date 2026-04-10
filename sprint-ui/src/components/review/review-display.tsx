import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import type { SprintReview } from "@/types/review";

export function ReviewDisplay({ review }: { review: SprintReview }) {
  return (
    <div className="space-y-6">
      <Card className="bg-ink text-sand">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <p className="text-xs uppercase tracking-[0.24em] text-sand/60">Sprint review</p>
            <h2 className="mt-2 font-display text-3xl font-bold">{review.summary.title}</h2>
            <p className="mt-3 max-w-3xl text-sm text-sand/80">{review.summary.overview}</p>
          </div>
          <Badge className="bg-sand text-ink">{review.status}</Badge>
        </div>
        <div className="mt-6 grid gap-4 md:grid-cols-3">
          <div>
            <p className="text-xs uppercase tracking-[0.18em] text-sand/60">Delivery</p>
            <p className="mt-2 text-sm text-sand/90">{review.summary.deliverySummary}</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-[0.18em] text-sand/60">Quality</p>
            <p className="mt-2 text-sm text-sand/90">{review.summary.qualitySummary}</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-[0.18em] text-sand/60">Outcome</p>
            <p className="mt-2 text-sm text-sand/90">{review.summary.outcomeSummary}</p>
          </div>
        </div>
      </Card>

      <div className="grid gap-6 xl:grid-cols-[1.3fr_0.9fr]">
        <div className="space-y-6">
          <Card>
            <SectionTitle title="Themes" subtitle="How the sprint work clusters into product and engineering narratives." />
            <div className="mt-4 grid gap-4">
              {review.themes.map((theme) => (
                <div key={theme.name} className="rounded-3xl border border-line bg-cloud p-4">
                  <div className="flex items-center justify-between gap-3">
                    <h3 className="font-display text-xl font-semibold text-ink">{theme.name}</h3>
                    <Badge>{theme.relatedIssueKeys.length} issues</Badge>
                  </div>
                  <p className="mt-2 text-sm text-stone-700">{theme.description}</p>
                  <p className="mt-3 text-xs uppercase tracking-[0.16em] text-stone-500">{theme.relatedIssueKeys.join(" • ")}</p>
                </div>
              ))}
            </div>
          </Card>

          <Card>
            <SectionTitle title="Highlights" subtitle="Presentation-ready wins tied back to real tickets." />
            <div className="mt-4 grid gap-4">
              {review.highlights.map((highlight) => (
                <div key={highlight.title} className="rounded-3xl border border-line p-4">
                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <h3 className="text-lg font-semibold text-ink">{highlight.title}</h3>
                    <Badge tone="success">{highlight.category}</Badge>
                  </div>
                  <p className="mt-2 text-sm text-stone-700">{highlight.description}</p>
                  <p className="mt-3 text-xs uppercase tracking-[0.16em] text-stone-500">{highlight.relatedIssueKeys.join(" • ")}</p>
                </div>
              ))}
            </div>
          </Card>
        </div>

        <div className="space-y-6">
          <Card>
            <SectionTitle title="Blockers" subtitle="Risks and carry-over work to call out explicitly." />
            <div className="mt-4 grid gap-4">
              {review.blockers.length === 0 ? (
                <p className="text-sm text-stone-600">No blockers captured in the latest review.</p>
              ) : (
                review.blockers.map((blocker) => (
                  <div key={blocker.title} className="rounded-3xl border border-rose-100 bg-rose-50 p-4">
                    <div className="flex items-center justify-between gap-3">
                      <h3 className="text-base font-semibold text-ink">{blocker.title}</h3>
                      <Badge tone={blocker.severity === "HIGH" ? "danger" : blocker.severity === "MEDIUM" ? "warning" : "default"}>
                        {blocker.severity}
                      </Badge>
                    </div>
                    <p className="mt-2 text-sm text-stone-700">{blocker.description}</p>
                    <p className="mt-3 text-xs uppercase tracking-[0.16em] text-stone-500">{blocker.relatedIssueKeys.join(" • ")}</p>
                  </div>
                ))
              )}
            </div>
          </Card>

          <Card>
            <SectionTitle title="Speaker notes" subtitle="A lightweight script for the live sprint review." />
            <div className="mt-4 space-y-3">
              {review.speakerNotes
                .slice()
                .sort((left, right) => left.displayOrder - right.displayOrder)
                .map((note) => (
                  <div key={`${note.section}-${note.displayOrder}`} className="rounded-3xl border border-line bg-cloud p-4">
                    <p className="text-xs uppercase tracking-[0.18em] text-stone-500">{note.section}</p>
                    <p className="mt-2 text-sm text-stone-700">{note.note}</p>
                  </div>
                ))}
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}

function SectionTitle({ title, subtitle }: { title: string; subtitle: string }) {
  return (
    <div>
      <h2 className="font-display text-2xl font-bold text-ink">{title}</h2>
      <p className="mt-2 text-sm text-stone-600">{subtitle}</p>
    </div>
  );
}
